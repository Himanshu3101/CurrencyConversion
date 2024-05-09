package com.example.currencyconversion.network.server.retrofit

import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.server.retrofit.API
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(val api: API) {

    suspend fun getData(apiKey: String) = api.getLatestData(apiKey)

}








/*    suspend fun fetchData(apiKey:String): Flow<ResponseExchangeList> = flow {
        val response = api.getLatestData(apiKey)
        if (response.isSuccessful) {
            response.body()?.let { emit(it) }
        } else {
            throw Exception(response.message())
        }
    }.catch { e ->
        emit(listOf()) // Optionally handle the error or emit an empty list
        throw e
    }*/

