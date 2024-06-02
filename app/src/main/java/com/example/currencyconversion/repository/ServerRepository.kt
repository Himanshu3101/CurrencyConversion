package com.example.currencyconversion.repository

import android.util.Log
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.toResultFlow
import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.network.server.API
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import com.example.currencyconversion.repository.interfaces.ServerDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class ServerRepository @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val api: API,
    private val roomRepository: LocalDataRepository,

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
                    if (rateCard.all { rate -> rate.rate != null }) {
                        roomRepository.insertDBExchangeData(rateCard)
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


