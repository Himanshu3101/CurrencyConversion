package com.example.currencyconversion.repository

import android.util.Log
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.toResultFlow
import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.network.Database.CurrencyDataBase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface ServerDataRepository {
    suspend fun getServerExchangeRates(apiKey: String): Flow<NetworkResult<ResponseExchangeList>>

    suspend fun getServerExchangeCurrency(): Flow<NetworkResult<Map<String, String>>>
}

//FOr ROOM DB
interface LocalDataRepository {
    suspend fun insertDBExchangeData(rates: List<Rates>)/*: Boolean*/
    suspend fun insertCurrency(currency: List<Currency>)
    suspend fun getAllRates(): Rates
    suspend fun getAllCurrency(): List<String/*Currency*/>
    suspend fun isCurrencyTableEmpty(): Boolean
}

class ServerRepository @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val api: com.example.currencyconversion.network.server.retrofit.API,
    private val roomRepository:LocalDataRepository,
) : ServerDataRepository {
    override suspend fun getServerExchangeRates(apiKey: String): Flow<NetworkResult<ResponseExchangeList>> =
        flow {
            toResultFlow(context) {
                api.getLatestData(apiKey)
            }.collect { newData: NetworkResult<ResponseExchangeList> ->
                newData.data?.let {
                    val rateCard = it.rates.map { (rateCode, rate) ->
                        Rates(rateCode, rate)
                    }
                    if (rateCard.all { rate -> rate.rateCode != null && rate.rate != null }) {
                        roomRepository.insertDBExchangeData(rateCard)
                        Log.d("Repository", "saveResult $rateCard")
                    } else {
                        Log.e("Repository", "Null values found in rates data: $rateCard")
                    }
                }
                emit(newData)
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getServerExchangeCurrency(): Flow<NetworkResult<Map<String, String>>> =
        flow {
            toResultFlow(context) {
                Log.d("Repository", "response currency")
                api.getCurrencies()
            }.collect { newData: NetworkResult<Map<String, String>> ->
                newData.data?.let {
                    val currencies = it.map { (code, name) ->
                        Currency(code, name)
                    }
                    roomRepository.insertCurrency(currencies)
                }
                emit(newData)
            }
        }.flowOn(Dispatchers.IO)
}


//FOr ROOM DB
class ROOMRepository @Inject constructor(private val currencyDataBase: CurrencyDataBase) :
    LocalDataRepository {

    override suspend fun insertDBExchangeData(rates: List<Rates>){
        return currencyDataBase.currencyDao().insertData(rates)

    }

    override suspend fun insertCurrency(currencies: List<Currency>) {
        currencyDataBase.currencyDao().insertCurrencies(currencies)
    }

    override suspend fun getAllRates(): Rates {
        return currencyDataBase.currencyDao().getAllData()
    }



    override suspend fun getAllCurrency(): List<String> {
        return currencyDataBase.currencyDao().getCurrencies()
    }

    override suspend fun isCurrencyTableEmpty(): Boolean {
        return currencyDataBase.currencyDao().getCurrencyCount() == 0
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










