package com.example.currencyconversion.network.server

import com.example.currencyconversion.di.IoDispatcher
import com.example.currencyconversion.utils.Constants.Companion.API_INTERNET_MESSAGE
import com.example.currencyconversion.utils.NetworkConnectivity
import com.example.currencyconversion.utils.NetworkConnectivityImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response


inline fun <reified T> toResultFlow(networkConnectivity: NetworkConnectivity, @IoDispatcher dispatcher: CoroutineDispatcher, crossinline call: suspend () -> Response<T>?): Flow<NetworkResult<T>> {

    return flow {
        val isInternetConnected = networkConnectivity.hasInternetConnection()
        println("Internet Connection Check: $isInternetConnected")
        if (isInternetConnected) {
            emit(NetworkResult.Loading(true))
            val response = call()
            response?.let { response ->
                try {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let {
                            emit(NetworkResult.Success(it))
                        }
                    }else{

                        val errorMsg = response.errorBody()?.string().orEmpty()
                        response.errorBody()?.close()
                        emit(NetworkResult.Error(errorMsg))

                    }
                } catch (e: Exception) {
                    emit(NetworkResult.Error(e.message.orEmpty()))
                } finally {
                    emit(NetworkResult.Loading(false))
                }
            } ?: emit(NetworkResult.Error("Response was null"))
        } else {
            emit(NetworkResult.Error(API_INTERNET_MESSAGE))
        }
    }.flowOn(dispatcher)
}