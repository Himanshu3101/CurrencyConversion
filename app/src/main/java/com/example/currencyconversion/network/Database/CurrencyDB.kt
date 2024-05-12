package com.example.currencyconversion.network.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.data.models.ResponseExchangeList

@Database(entities = [Rates::class], version = 1)
abstract class CurrencyDB : RoomDatabase() {

    abstract fun getCurrencyConversion() : CurrencyDAO
}