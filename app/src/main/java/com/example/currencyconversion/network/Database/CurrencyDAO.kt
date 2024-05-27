package com.example.currencyconversion.network.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates

@Dao
interface CurrencyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(rates: Rates):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: Currency) : Long

    @Query("SELECT * FROM exchange_rates")
    suspend fun getAllData() : Rates

    @Query("SELECT * FROM currencies")
    suspend fun getCurrencies(): Currency

    @Query("SELECT COUNT(*) FROM currencies")
    suspend fun getCurrencyCount(): Int
}