package com.example.currencyconversion.network.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.data.models.ResponseExchangeList

@Dao
interface CurrencyDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE) //for same data store ignore
    suspend fun addData(rates: Rates)

    @Query("SELECT * FROM Rates")
    fun getAllData() : LiveData<List<Rates>>
}