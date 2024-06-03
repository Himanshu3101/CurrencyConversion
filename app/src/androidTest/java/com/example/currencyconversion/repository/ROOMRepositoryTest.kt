package com.example.currencyconversion.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.models.Rates
import com.example.currencyconversion.network.database.CurrencyDAO
import com.example.currencyconversion.network.database.CurrencyDataBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ROOMRepositoryTest {

    private lateinit var database: CurrencyDataBase
    private lateinit var dao: CurrencyDAO
    private lateinit var repository: ROOMRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CurrencyDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.currencyDao()
        repository = ROOMRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndGetRates() = runTest(testDispatcher) {
        val rates = listOf(
            Rates("USD", 1.0),
            Rates("EUR", 0.85)
        )

        repository.insertDBExchangeData(rates)

        val rate = repository.getAllRates("USD")
        assertNotNull(rate)
        rate?.let { assertEquals(1.0, it, 0.001) }
    }

    @Test
    fun testInsertAndGetCurrencies() = runTest(testDispatcher) {
        val currencies = listOf(
            Currency("USD", "United States Dollar"),
            Currency("EUR", "Euro")
        )

        repository.insertCurrency(currencies)

        val currencyList = repository.getAllCurrency()
        assertNotNull(currencyList)
        assertEquals(2, currencyList.size)
        assertTrue(currencyList.contains("USD"))
        assertTrue(currencyList.contains("EUR"))
    }

    @Test
    fun testIsCurrencyTableEmpty() = runTest(testDispatcher) {
        var isEmpty = repository.isCurrencyTableEmpty()
        assertTrue(isEmpty)

        val currencies = listOf(
            Currency("USD", "United States Dollar"),
            Currency("EUR", "Euro")
        )
        repository.insertCurrency(currencies)

        isEmpty = repository.isCurrencyTableEmpty()
        assertFalse(isEmpty)
    }
}