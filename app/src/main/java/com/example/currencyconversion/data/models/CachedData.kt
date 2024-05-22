package com.example.currencyconversion.data.models

import com.example.currencyconversion.network.server.NetworkResult
import kotlinx.coroutines.flow.Flow

data class CachedData(val data: Flow<NetworkResult<ResponseExchangeList>>, val fetchTime: Long)
