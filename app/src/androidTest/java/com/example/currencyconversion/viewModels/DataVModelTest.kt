package com.example.currencyconversion.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.currencyconversion.MainActivity
import com.example.currencyconversion.models.Currency
import com.example.currencyconversion.network.di.IoDispatcher
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DataVModelTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()/* = StandardTestDispatcher()*/

    @Inject
    @IoDispatcher
    lateinit var testDispatcher: CoroutineDispatcher

    @Inject
    lateinit var localDataRepository: LocalDataRepository

    private lateinit var viewModel: DataVModel


    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)

        localDataRepository = mock(LocalDataRepository::class.java)

        val scenario = launchActivity<MainActivity>()
        scenario.onActivity { activity ->
            viewModel = ViewModelProvider(activity).get(DataVModel::class.java)
        }
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun testIsCurrencyTableEmpty() = runTest(testDispatcher) {
        `when`(localDataRepository.isCurrencyTableEmpty()).thenReturn(true)
        val result = viewModel.isCurrencyTableEmpty()
        assertEquals(true, result)
    }

    @Test
    fun testGetDBConversionCurrency() = runTest(testDispatcher) {
        // Given
        val currencyLists = listOf("USD", "EUR", "JPY")
        `when`(localDataRepository.getAllCurrency()).thenReturn(currencyLists)

        // When
        viewModel.getDBConversionCurrency()
        advanceUntilIdle()  // Ensures all pending coroutines are executed



      /*  val currencyLists = listOf("USD", "EUR", "JPY")
        `when`(localDataRepository.getAllCurrency()).thenReturn(currencyLists)
//        viewModel.getDBConversionCurrency()
        advanceUntilIdle()

        assertEquals(currencyLists, viewModel.dBConversionCurrency.first())*/

        // Then
        Assert.assertEquals(currencyLists, viewModel.dBConversionCurrency.first())
    }

   /* @Test
    fun testGetSelectedCurrencyRate() = runTest(testDispatcher) {
        // Given
        val rate = 1.23
        `when`(roomRepository.getAllRates("USD")).thenReturn(rate)

        // When
        val result = viewModel.getSelectedCurrencyRate("USD")

        // Then
        Assert.assertEquals(rate, result)
    }*/
}