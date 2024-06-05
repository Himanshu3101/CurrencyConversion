package com.example.currencyconversion.network.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates

@Dao
interface CurrencyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRateData(currencyRates: List<Rates>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<Currency>)

    @Query("SELECT rate FROM exchange_rates where rateCode = :currencyCode")
    suspend fun getRateData(currencyCode: String?): Double?

    @Query("SELECT currencyCode FROM currencies")
    suspend fun getCurrencies(): List<String>

    @Query("SELECT COUNT(*) FROM currencies")
    suspend fun getCurrencyCount(): Int

//     For Testing
    @Query("SELECT name FROM currencies where currencyCode = :currencyCode")
    suspend fun getCurrencyData(currencyCode: String?): String?


}