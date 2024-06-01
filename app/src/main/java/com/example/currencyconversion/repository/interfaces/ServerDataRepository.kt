package com.example.currencyconversion.repository.interfaces

import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.network.server.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ServerDataRepository {
    suspend fun getServerExchangeRates(apiKey: String): Flow<NetworkResult<ResponseExchangeList>>
    suspend fun getServerExchangeCurrency(): Flow<NetworkResult<Map<String, String>>>
}