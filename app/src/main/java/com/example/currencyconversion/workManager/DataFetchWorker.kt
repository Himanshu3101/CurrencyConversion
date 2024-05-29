package com.example.currencyconversion.workManager

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.currencyconversion.BuildConfig
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.utils.NetworkResultDeserializer
import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.network.di.NetworkModule
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.retrofit.API
import com.example.currencyconversion.repository.ServerRepository
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
            val exchangeRate = ServerRepository(context, remoteDataSource, roomRepository)

            exchangeRate.getServerExchangeRates(BuildConfig.API_KEY).collect { result ->
                result.data?.let {
                    Log.d("DataFetchWorker", "Fetched data Exchange Rates: ${result.data.rates}")




                    /*val intent = Intent("com.example.UPDATE_DATA")
                    intent.putExtra("basedCurrency", it.rates["USD"])
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)*/
                }
            }

            exchangeRate.getServerExchangeCurrency().collect { result ->
                result.data?.let {
                    val currency = roomRepository.getAllCurrency()
                    val intent = Intent("com.example.UPDATE_DATA")
                    val arrayOfStrings = currency.toTypedArray()
                    intent.putExtra("currencyData", arrayOfStrings)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    Log.d("DataFetchWorker", "Fetched data Exchange Currency: $currency")
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