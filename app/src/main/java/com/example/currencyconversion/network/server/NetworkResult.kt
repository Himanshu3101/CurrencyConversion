package com.example.currencyconversion.network.server

sealed class NetworkResult <out T> (val status: ApiStatus, val data: T?, val message:String?) {

    data class Success<out T>(val _data:T?): NetworkResult<T>(status = ApiStatus.SUCCESS, data = _data, message = null)

    data class Error(val exception: String): NetworkResult<Nothing>(status = ApiStatus.ERROR, data = null, message = exception)

    data class Loading<out T>(val isLoading:Boolean): NetworkResult<T>(status = ApiStatus.LOADING, data = null, message = null)
}

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}