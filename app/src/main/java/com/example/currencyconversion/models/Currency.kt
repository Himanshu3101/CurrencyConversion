package com.example.currencyconversion.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.Nonnull

@Entity(tableName = "currencies")
data class Currency(
    @Nonnull
    @PrimaryKey val currencyCode: String,
    val name: String?
)