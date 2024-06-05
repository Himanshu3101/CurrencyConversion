package com.example.currencyconversion.adapter

import androidx.work.Configuration
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.currencyconversion.CurrencyConversion

class TestCurrencyConversion : CurrencyConversion() {
    override fun onCreate() {
        // Initialize WorkManager here for test environment
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(this, config)
        super.onCreate()
    }
}