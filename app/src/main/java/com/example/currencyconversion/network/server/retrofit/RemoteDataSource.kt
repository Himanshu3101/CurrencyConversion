package com.example.currencyconversion.network.server.retrofit

import javax.inject.Inject

class RemoteDataSource @Inject constructor(val api: API) {
    suspend fun getData(apiKey: String) = api.getLatestData(apiKey)

}

