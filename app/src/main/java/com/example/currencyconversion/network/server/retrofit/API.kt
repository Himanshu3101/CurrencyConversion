package com.example.currencyconversion.network.server.retrofit

import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.ResponseExchangeList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface API {

    @Headers(
        "Content-Type: application/json"
    )
    @GET("latest.json")
    suspend fun getLatestData(@Query ("app_id") apiKey:String) : Response<ResponseExchangeList>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("currencies.json")
    suspend fun getCurrencies() : Response<Currency>
}