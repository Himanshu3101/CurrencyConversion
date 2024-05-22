package com.example.currencyconversion.repository

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.currencyconversion.network.di.NetworkModule
import com.example.currencyconversion.network.server.retrofit.API
import com.example.currencyconversion.network.server.retrofit.RemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import retrofit2.create
import javax.inject.Inject

@HiltWorker
class DataFetchWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DataFetchWorker", "Started fetching data")

            val cache = Cache()
            val remoteDataSource = NetworkModule.provideRetrofit().create(API::class.java)
            val serverRepository = NetworkModule.provideServerRepository(context, remoteDataSource)
            val dataRepository = DataRepository(serverRepository, cache)
            val data = dataRepository.getData()/*.single()*/ // Assuming getData() returns a Flow


            Log.d("DataFetchWorker", "Fetched data: $data")
            Log.d("DataFetchWorker", "Finished fetching data")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataFetchWorker", "Error fetching data", e)
            Result.failure()
        }
    }
}