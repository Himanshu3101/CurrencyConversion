package com.example.currencyconversion

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyconversion.workManager.DataFetchWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class CurrencyConversion : Application(){

    override fun onCreate() {
        super.onCreate()
        // Initialize other components
        schedulePeriodicDataFetch()
    }

    private fun schedulePeriodicDataFetch() {
        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraint)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DataFetchWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}