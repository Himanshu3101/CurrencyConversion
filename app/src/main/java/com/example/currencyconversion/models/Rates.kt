package com.example.currencyconversion.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.Nonnull


@Entity(tableName = "exchange_rates")
data class Rates(
    @Nonnull
    @PrimaryKey val rateCode: String,
    val rate: Double?
)


