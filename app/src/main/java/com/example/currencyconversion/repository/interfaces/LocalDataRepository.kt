package com.example.currencyconversion.repository.interfaces

import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates

interface LocalDataRepository {
    suspend fun insertDBExchangeData(rates: List<Rates>)
    suspend fun insertCurrency(currency: List<Currency>)
    suspend fun getAllRates(currencyCountry: String?): Double?
    suspend fun getAllCurrency(): List<String>
    suspend fun isCurrencyTableEmpty(): Boolean
}