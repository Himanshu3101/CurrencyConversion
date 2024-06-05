package com.example.currencyconversion.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkConnectivityImpl @Inject constructor(private val context: Context) : NetworkConnectivity{

    override fun hasInternetConnection(): Boolean {
        return Constants.hasInternetConnection(context)
    }
}