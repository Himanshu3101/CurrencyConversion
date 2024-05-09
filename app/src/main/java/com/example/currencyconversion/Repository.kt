package com.example.currencyconversion

import android.content.Context
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.network.server.toResultFlow
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.server.retrofit.RemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ConversionRepository {

    suspend fun getExchangeData(apiKey: String)

    suspend fun insertDBExchangeData(rates: Rates)
}


/*@ApplicationContext private val context: Context, - used by Hilt to provide the application-wide context rather than an activity-specific context.*/
class Repository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteDataSource: RemoteDataSource/*,
    private val dao: ConversionDAO*/
) {

    suspend fun getExchangeData(apiKey: String): Flow<NetworkResult<ResponseExchangeList>> {
        return toResultFlow(context) {
            remoteDataSource.getData(apiKey)
        }
    }


}