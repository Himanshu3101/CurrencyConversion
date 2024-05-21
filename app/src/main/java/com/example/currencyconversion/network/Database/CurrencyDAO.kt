package com.example.currencyconversion.network.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconversion.data.models.Rates

@Dao
interface CurrencyDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(rates: Rates):Long

    @Query("SELECT * FROM Rates")
    suspend fun getAllData() : List<Rates>/*kotlinx.coroutines.flow.Flow<List<Rates>>*/
}