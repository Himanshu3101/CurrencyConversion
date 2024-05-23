package com.example.currencyconversion.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.currencyconversion.network.di.NetworkModule
import com.example.currencyconversion.network.server.retrofit.API
import com.example.currencyconversion.repository.ServerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataFetchWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DataFetchWorker", "Started fetching data")

            val remoteDataSource = NetworkModule.provideRetrofit().create(API::class.java)
//            val serverRepository = NetworkModule.provideServerRepository(context, remoteDataSource)
            ServerRepository(context, remoteDataSource).getServerExchangeData()

            /*val dataRepository =  DataRepository(serverRepository, cache).getData()
            dataRepository.getData().collect { networkResult ->
               // Handle each emitted value from the Flow
               when (networkResult) {
                   is NetworkResult.Success -> {
                       val rates = networkResult.data?.rates
                       Log.d("DataFetchWorker", "Fetched data: $rates")
                       // Process the fetched data, e.g., cache it or update the database
                   }
                   is NetworkResult.Error -> {
                       Log.e("DataFetchWorker", "Error: ${networkResult.message}")
                   }

                   else -> Log.e("DataFetchWorker", "Something Problem Occured.")
               }
           }
*/
            Log.d("DataFetchWorker", "Finished fetching data")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataFetchWorker", "Error fetching data", e)
            Result.failure()
        }
    }
}