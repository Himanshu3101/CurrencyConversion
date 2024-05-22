package com.example.currencyconversion

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyconversion.repository.DataFetchWorker
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
        val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(2, TimeUnit.MINUTES)
            .setConstraints(constraint)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DataFetchWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )



        /*val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(
            repeatInterval = 2, // Repeat every 30 minutes
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(TAG_DataFetchWorked)
            .build()

        if (applicationContext != null) {
            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    "DataFetchPeriodic",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    periodicWorkRequest
                )
        }*/
    }
}