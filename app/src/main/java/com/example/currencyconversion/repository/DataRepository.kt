package com.example.currencyconversion.repository

import com.example.currencyconversion.BuildConfig
import com.example.currencyconversion.Utils.Cache
import com.example.currencyconversion.data.models.CachedData
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.server.NetworkResult
import kotlinx.coroutines.flow.Flow

import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DataRepository @Inject constructor (private val serverRepository: ServerRepository, private val cache: Cache) {

        suspend fun getData(): Flow<NetworkResult<ResponseExchangeList>>{
            val cachedData = cache.getCachedData()
            val currentTime = System.currentTimeMillis()

            return if (cachedData != null && !isDataExpired(cachedData, currentTime)) {
                cachedData.data
            } else {
                val newData = serverRepository.getServerExchangeData(BuildConfig.API_KEY)
                cache.saveCachedData(CachedData(newData, currentTime))
                newData
            }
        }

    private fun isDataExpired(data: CachedData, currentTime: Long): Boolean {
        val elapsedTime = currentTime - data.fetchTime
        return elapsedTime >= TimeUnit.MINUTES.toMillis(15) // Check if 30 minutes have passed
    }
}