package com.example.currencyconversion.repository

import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates
import com.example.currencyconversion.network.database.CurrencyDataBase
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import javax.inject.Inject

open class ROOMRepository @Inject constructor(private val currencyDataBase: CurrencyDataBase) :
    LocalDataRepository {

    // Rates Insertion from Server
    override suspend fun insertDBExchangeData(rates: List<Rates>){
        return currencyDataBase.currencyDao().insertRateData(rates)

    }

    // Currency Insertion from Server
    override suspend fun insertCurrency(currencies: List<Currency>) {
        currencyDataBase.currencyDao().insertCurrencies(currencies)
    }

    // Get Currency List from DB
    override suspend fun getAllCurrency(): List<String> {
        return currencyDataBase.currencyDao().getCurrencies()
    }

    //Frr App Validation from DB
    override suspend fun isCurrencyTableEmpty(): Boolean {
        return currencyDataBase.currencyDao().getCurrencyCount() == 0
    }

    override suspend fun getAllRates(currencyCountry: String?): Double? {
        return currencyDataBase.currencyDao().getRateData(currencyCountry)
    }
}