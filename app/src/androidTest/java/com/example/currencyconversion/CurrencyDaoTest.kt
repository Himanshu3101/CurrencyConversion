package com.example.currencyconversion

import android.app.Application
import android.provider.Telephony.Mms.Rate
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.currencyconversion.models.Rates
import com.example.currencyconversion.network.database.CurrencyDAO
import com.example.currencyconversion.network.database.CurrencyDataBase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import  com.example.currencyconversion.models.Currency
import org.junit.Assert.assertNull


class CurrencyDaoTest {

    lateinit var currencyDAO: CurrencyDAO
    lateinit var currencyDataBase: CurrencyDataBase

    @Before
    fun setup() {
        currencyDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), CurrencyDataBase::class.java
        ).allowMainThreadQueries().build()
        currencyDAO = currencyDataBase.currencyDao()
    }

    @Test
    fun insertRate_expectedList() = runBlocking{
        val rateList = listOf(
            Rates("USD", 1.0),
            Rates("INR", 83.460852),
        )
        currencyDAO.insertRateData(rateList)

        val usdRate: Double? = currencyDAO.getRateData("USD")
        val eurRate:Double? = currencyDAO.getRateData("INR")

        assertEquals(1.0, usdRate)
        assertEquals(83.460852,eurRate)
    }

    @Test
    fun insertRate_replacesOnConflict() = runBlocking {
        val initialRates = listOf(
            Rates("USD", 1.0)
        )
        currencyDAO.insertRateData(initialRates)
        val updatedRates = listOf(
            Rates("USD", 1.1)
        )

        currencyDAO.insertRateData(updatedRates)
        val usdRate = currencyDAO.getRateData("USD")
        assertEquals(1.1, usdRate)
    }

    @Test
    fun insertCurrency_expectedList() = runBlocking{
        val currencyList = listOf(
            Currency("USD", "United States Dollar"),
            Currency("INR", "Indian Rupee")
        )
        currencyDAO.insertCurrencies(currencyList)


        //
        val usdRate = currencyDAO.getCurrencyData("USD")
        val eurRate = currencyDAO.getCurrencyData("INR")

        assertEquals("United States Dollar", usdRate)
        assertEquals("Indian Rupee",eurRate)
    }

    @Test
    fun insertCurrency_replacesOnConflict() = runBlocking {
        val initialCurrency = listOf(
            Currency("USD", "United States Dollar"),
        )
        currencyDAO.insertCurrencies(initialCurrency)
        val updatedCurrency = listOf(
            Currency("USD", "United States")
        )

        currencyDAO.insertCurrencies(updatedCurrency)

        val currency = currencyDAO.getCurrencyData("USD")
        assertEquals("United States", currency)
    }

    @Test
    fun getRateData_rateCode () = runBlocking{
        val rateList = listOf(
            Rates("USD", 1.0),
            Rates("INR", 83.460852),
        )
        currencyDAO.insertRateData(rateList)
        val usdRate: Double? = currencyDAO.getRateData("INR")
        assertEquals(83.460852, usdRate)
    }

    @Test
    fun getRateData_returnsNullForNonexistentCRateCode() = runBlocking {
        val rateList = listOf(
            Rates("USD", 1.0),
            Rates("INR", 83.460852),
        )

        currencyDAO.insertRateData(rateList)
        val nonExistentRate = currencyDAO.getRateData("GBP")
        assertNull(nonExistentRate)
    }

    @Test
    fun getRateData_handlesNullRateCode() = runBlocking {
        val rateList = listOf(
            Rates("USD", 1.0),
            Rates("INR", 83.460852),
        )
        currencyDAO.insertRateData(rateList)
        assertNull(currencyDAO.getRateData(null))
    }

    @Test
    fun getCurrencies_list () = runBlocking{
        val currencyList = listOf(
            Currency("USD", "United States Dollar"),
            Currency("INR", "Indian Rupee")
        )
        currencyDAO.insertCurrencies(currencyList)

        val result = currencyDAO.getCurrencies()
        assertEquals(2, result.size)
        assertEquals("INR", result[0])
        assertEquals("USD", result[1])
    }

    @Test
    fun getCurrencies_returnsEmptyListWhenNoData() = runBlocking {
        assertEquals(0, currencyDAO.getCurrencies().size)
    }

    @Test
    fun getCurrenciesCount_returnsCorrectCount () = runBlocking{
        val currencyList = listOf(
            Currency("USD", "United States Dollar"),
            Currency("INR", "Indian Rupee")
        )
        currencyDAO.insertCurrencies(currencyList)
        assertEquals(2, currencyDAO.getCurrencyCount())
    }

    @Test
    fun getCurrencyCount_returnsZeroWhenNoData() = runBlocking {
        assertEquals(0, currencyDAO.getCurrencyCount())
    }

    @After
    fun tearDown() {
        currencyDataBase.close()
    }
}