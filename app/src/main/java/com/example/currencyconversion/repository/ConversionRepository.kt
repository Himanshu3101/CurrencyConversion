package com.example.currencyconversion.repository

import android.content.Context
import androidx.lifecycle.map
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.toResultFlow
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.Database.CurrencyDB
import com.example.currencyconversion.network.server.retrofit.RemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

interface ServerDataRepository  {
    suspend fun getServerExchangeData(apiKey: String): kotlinx.coroutines.flow.Flow<NetworkResult<ResponseExchangeList>>
}


interface LocalDataRepository{
    suspend fun insertDBExchangeData(rates: Rates){}
    fun getDBExchangeData(){}
}

class ServerRepository @Inject constructor(@ApplicationContext private val context: Context, private val remoteDataSource: RemoteDataSource, ) : ServerDataRepository{
    override suspend fun getServerExchangeData(apiKey: String): kotlinx.coroutines.flow.Flow<NetworkResult<ResponseExchangeList>> {
        return toResultFlow(context) {
            remoteDataSource.getData(apiKey)
        }
    }
}

class ROOMRepository @Inject constructor(private val currencyDB: CurrencyDB):LocalDataRepository{

    override suspend fun insertDBExchangeData(rates: Rates) {
        currencyDB.getCurrencyConversion().addData(rates)
    }

    override fun getDBExchangeData() {
        currencyDB.getCurrencyConversion().getAllData()
        return
    }

}