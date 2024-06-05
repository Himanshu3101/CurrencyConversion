package com.example.currencyconversion.network.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates

@Database(entities = [Currency::class, Rates:: class], version = 1)
abstract class CurrencyDataBase : RoomDatabase() {

    abstract fun currencyDao() : CurrencyDAO
}