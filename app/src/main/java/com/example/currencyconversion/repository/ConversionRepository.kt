package com.example.currencyconversion.repository

import android.content.Context
import android.util.Log
import com.example.currencyconversion.BuildConfig
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.toResultFlow
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.Database.CurrencyDataBase
import com.example.currencyconversion.network.server.retrofit.API
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ServerDataRepository {
    suspend fun getServerExchangeData(/*apiKey: String*/): Flow<NetworkResult<ResponseExchangeList>>
}

//FOr ROOM DB
interface LocalDataRepository {
    suspend fun insertDBExchangeData(rates: Rates): Boolean
    suspend fun getAll(): List<Rates>/*Flow<List<Rates>>*/
}

class ServerRepository @Inject constructor (@ApplicationContext private val context: Context, private val api: API) :ServerDataRepository {
    override suspend fun getServerExchangeData(): Flow<NetworkResult<ResponseExchangeList>> = flow {
        /*  val cachedData = cache.getCachedData()
        val currentTime = System.currentTimeMillis()

        if (cachedData != null && !isDataExpired(cachedData, currentTime)) {
            emit(cachedData.data)
        } else {*/
        // Collect the Flow returned by toResultFlow and emit each value
        toResultFlow(context) {
            api.getLatestData(BuildConfig.API_KEY)
        }/*.collect { newData ->
                cache.saveCachedData(CachedData(newData, currentTime))
                emit(newData)
            }
        }
    }.flowOn(Dispatchers.IO)*/

        /*private fun isDataExpired(data: CachedData, currentTime: Long): Boolean {
        val elapsedTime = currentTime - data.fetchTime
        return elapsedTime >= TimeUnit.MINUTES.toMillis(15) // Check if 30 minutes have passed
    }*/
    }
}


/*private fun isDataExpired(data: CachedData): Boolean {
    val currentTime = System.currentTimeMillis()
    val elapsedTime = currentTime - data.fetchTime
    return elapsedTime >= TimeUnit.MINUTES.toMillis(15) // Check if 30 minutes have passed
}*/



    //FOr ROOM DB
    class ROOMRepository @Inject constructor(private val currencyDataBase: CurrencyDataBase) : LocalDataRepository {

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








/*class ServerRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: API,
) : ServerDataRepository {
    override suspend fun getServerExchangeData(apiKey: String): Flow<NetworkResult<ResponseExchangeList>> {
        return toResultFlow(context) {
            api.getLatestData(apiKey)
        }
    }
}*/















