package com.example.currencyconversion.workManager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.currencyconversion.BuildConfig
import com.example.currencyconversion.Utils.NetworkResultDeserializer
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.Database.CurrencyDataBase
import com.example.currencyconversion.network.di.NetworkModule
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.retrofit.API
import com.example.currencyconversion.repository.ServerRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
//import kotlinx.coroutines.flow.internal.NopCollector.emit

@HiltWorker
class DataFetchWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DataFetchWorker", "Started fetching data")

            val remoteDataSource = NetworkModule.provideRetrofit().create(API::class.java)
            val currencyDB = NetworkModule.provideDatabase(context)
            val roomRepository= NetworkModule.provideDataRepository(currencyDB)
            val repos = ServerRepository(context, remoteDataSource, roomRepository)

            repos.getServerExchangeData(BuildConfig.API_KEY).collect { result ->
                result.data?.let {

                    Log.d("DataFetchWorker", "Fetched data: ${result.data?.rates}")

                    val gson = GsonBuilder()
                        .registerTypeAdapter(object : TypeToken<NetworkResult<ResponseExchangeList>>() {}.type, NetworkResultDeserializer())
                        .create()

                    val resultJson = gson.toJson(result)

                    val intent = Intent("com.example.UPDATE_DATA")
                    intent.putExtra("data", resultJson)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    Log.d("DataFetchWorker", "Result Handled WM")

                }
            }

            Log.d("DataFetchWorker", "Finished fetching data")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataFetchWorker", "Error fetching data", e)
            Result.failure()
        }
    }
}

