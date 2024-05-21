package com.example.currencyconversion.network.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyconversion.data.models.Rates

@Database(entities = [Rates::class], version = 1)
abstract class CurrencyDataBase : RoomDatabase() {

    abstract fun currencyDao() : CurrencyDAO
}