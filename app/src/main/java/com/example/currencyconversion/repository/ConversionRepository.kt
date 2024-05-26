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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface ServerDataRepository {
    suspend fun getServerExchangeData(apiKey: String): Flow<NetworkResult<ResponseExchangeList>>
}

//FOr ROOM DB
interface LocalDataRepository {
    suspend fun insertDBExchangeData(rates: Rates): Boolean
    suspend fun getAllRates(): Rates
}

class ServerRepository @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val api: com.example.currencyconversion.network.server.retrofit.API,
    private val roomRepository: /*com.example.currencyconversion.repository.ROOMRepository*/LocalDataRepository,
) : ServerDataRepository {
    override suspend fun getServerExchangeData(apiKey: String): Flow<NetworkResult<ResponseExchangeList>> =
        flow {
            val currentTime = System.currentTimeMillis()

                Log.d("Repository", "Server")
                toResultFlow(context) {
                    api.getLatestData(apiKey)
                }.collect { newData: NetworkResult<ResponseExchangeList> ->
                    newData.data?.let {
                        newData.data?.rates?.Time = currentTime
                        newData.data?.rates?.id = 1
                        val re = roomRepository.insertDBExchangeData(it.rates)
                        Log.d("Repository", "savReslt $re")
                    }
                    emit(newData)
                }
        }.flowOn(Dispatchers.IO)
}


//FOr ROOM DB
class ROOMRepository @Inject constructor(private val currencyDataBase: CurrencyDataBase) :
    LocalDataRepository {

    override suspend fun insertDBExchangeData(rates: Rates): Boolean {
        return try {
            currencyDataBase.currencyDao().insertData(rates) > 0
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAllRates(): Rates {
        return currencyDataBase.currencyDao().getAllData()
    }
}


/*private fun isDataExpired(data: Rates, currentTime: Long): Boolean {
    val elapsedTime = currentTime - data.Time
    return elapsedTime >= TimeUnit.MINUTES.toMillis(16)
}*/

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




/*  getServerExchangeData()
val currentTime = System.currentTimeMillis()
           Log.d("Repository", "ConversionRepo")


           val rates = roomRepository.getAllRates()
           if (rates != null) {
               Log.d("Repository", "DB not null")
               val rates = roomRepository.getAllRates()
               var bol = isDataExpired(rates, currentTime)
               Log.d("Repository", "DB_beforeIf$bol")
               if (!bol*//*rates.let { isDataExpired(it, currentTime) }!!*//*) {
                    Log.d("Repository", "DB_afterIf$bol")
                    emit(
                        NetworkResult.Success(
                            ResponseExchangeList(
                                "",
                                "",
                                "",
                                rates,
                                currentTime.toInt()
                            )
                        )
                    )
                }
            } else {*/










