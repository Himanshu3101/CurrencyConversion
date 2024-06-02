package com.example.currencyconversion

import android.app.Application
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyconversion.workManager.DataFetchWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class CurrencyConversion : /*MultiDex*/Application() {

    override fun onCreate() {
        super.onCreate()
        schedulePeriodicDataFetch()
    }

    private fun schedulePeriodicDataFetch() {
         Log.d("DataFetchWorker", "Conversion")
        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(16, TimeUnit.MINUTES)
            .setConstraints(constraint)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "DataFetchWork",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

}