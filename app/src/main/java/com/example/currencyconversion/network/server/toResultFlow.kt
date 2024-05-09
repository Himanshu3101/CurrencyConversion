package com.example.currencyconversion.network.server

import android.content.Context
import com.example.currencyconversion.Utils.Constants
import com.example.currencyconversion.Utils.Constants.Companion.API_INTERNET_MESSAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response


inline fun <reified T> toResultFlow(
    context: Context,
    crossinline call: suspend () -> Response<T>?
): Flow<NetworkResult<T>> {
    return flow {
        val isInternetConnected = Constants.hasInternetConnection(context)
        if (isInternetConnected) {
            emit(NetworkResult.Loading(true))
            val c = call()
            c?.let { response ->
                try {
                    if (response.isSuccessful && response.body() != null) {
                        c.body()?.let {
                            emit(NetworkResult.Success(it))
                        }
                    }else{

                        val errorMsg = response.errorBody()?.string().orEmpty()
                        response.errorBody()?.close()
                        emit(NetworkResult.Error(errorMsg))

                    }
                } catch (e: Exception) {
                    val errorMsg = response.errorBody()?.string().orEmpty()
                    emit(NetworkResult.Error(errorMsg))
                }
            }
        } else{
            emit(NetworkResult.Error(API_INTERNET_MESSAGE))
        }
    }.flowOn(Dispatchers.IO)
}