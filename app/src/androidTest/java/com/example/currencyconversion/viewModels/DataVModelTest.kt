package com.example.currencyconversion.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.currencyconversion.di.IoDispatcher
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
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DataVModelTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @IoDispatcher
    lateinit var testDispatcher: CoroutineDispatcher

    private lateinit var viewModel: DataVModel

    @Inject
    lateinit var mockLocalDataRepository: LocalDataRepository

    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)

        mockLocalDataRepository = Mockito.mock(LocalDataRepository::class.java)
        viewModel = DataVModel(mockLocalDataRepository, testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun test_IsCurrencyTableEmpty() = runTest(testDispatcher) {
        `when`(mockLocalDataRepository.isCurrencyTableEmpty()).thenReturn(true)
        val result = viewModel.isCurrencyTableEmpty()
        assertEquals(true, result)
    }

    @Test
    fun test_GetDBConversionCurrency() = runTest(testDispatcher) {
        val currencyLists = listOf("USD", "EUR", "JPY")
        `when`(mockLocalDataRepository.getAllCurrency()).thenReturn(currencyLists)
        viewModel.getDBConversionCurrency()
        advanceUntilIdle()
        assertEquals(currencyLists, viewModel.dBConversionCurrency.first())
    }

     @Test
     fun test_GetSelectedCurrencyRate() = runTest(testDispatcher) {
         val rate = 1.23
         `when`(mockLocalDataRepository.getAllRates("USD")).thenReturn(rate)
         val result = viewModel.getSelectedCurrencyRate("USD")
         Assert.assertEquals(rate, result)
     }
}