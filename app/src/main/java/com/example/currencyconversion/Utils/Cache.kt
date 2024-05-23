package com.example.currencyconversion.Utils

import com.example.currencyconversion.data.models.CachedData
import java.util.concurrent.TimeUnit

class Cache {
    private var cachedData: CachedData? = null
    private val lock = Any()

    fun getCachedData(): CachedData? {
        synchronized(lock) {
            return cachedData?.takeIf { !isDataExpired(it) }
        }
    }

    fun saveCachedData(data: CachedData) {
        synchronized(lock) {
            cachedData = data
        }
    }

    private fun isDataExpired(data: CachedData): Boolean {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - data.fetchTime
        return elapsedTime >= TimeUnit.MINUTES.toMillis(2) // Check if 30 minutes have passed
    }
}