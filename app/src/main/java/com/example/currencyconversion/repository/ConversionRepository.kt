package com.example.currencyconversion.repository

import android.content.Context
import android.util.Log
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.toResultFlow
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.Database.CurrencyDataBase
import com.example.currencyconversion.network.server.retrofit.API
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ServerDataRepository {
    suspend fun getServerExchangeData(apiKey: String): Flow<NetworkResult<ResponseExchangeList>>
}

interface LocalDataRepository {
    suspend fun insertDBExchangeData(rates: Rates): Boolean
    suspend fun getAll(): List<Rates>/*Flow<List<Rates>>*/
}

class ServerRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: API,
) : ServerDataRepository {
    override suspend fun getServerExchangeData(apiKey: String): Flow<NetworkResult<ResponseExchangeList>> {
        return toResultFlow(context) {
            api.getLatestData(apiKey)
        }
    }
}

class ROOMRepository @Inject constructor(private val currencyDataBase: CurrencyDataBase) :
    LocalDataRepository {

    override suspend fun insertDBExchangeData(rates: Rates): Boolean {
        return try {
            currencyDataBase.currencyDao().insertData(rates) > 0
        } catch (e: Exception) {
            Log.e("error_dbRepository", "Failed to add data", e)
            false
        }
    }

    override suspend fun getAll(): List<Rates>/*Flow<List<Rates>>*/ {
        return currencyDataBase.currencyDao().getAllData()
    }
}







/*
class DataRepository(private val apiRepository: ApiRepository, private val cache: Cache) {

    suspend fun getData(): List<Rates> {
        val cachedData = cache.getCachedData()
        val currentTime = System.currentTimeMillis()
        return if (cachedData != null && currentTime - cachedData.fetchTime < TimeUnit.MINUTES.toMillis(30)) {
            cachedData.data
        } else {
            val newData = apiRepository.fetchRates()
            cache.saveCachedData(CachedData(newData, currentTime))
            newData
        }
    }
}

class DataFetchWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val repository = DataRepository(ApiRepository(), Cache())
        runBlocking {
            repository.getData()
        }
        return Result.success()
    }
}

fun schedulePeriodicDataFetch(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(30, TimeUnit.MINUTES).build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DataFetchWork",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}*/
