package com.example.currencyconversion.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyconversion.di.IoDispatcher
import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.network.server.API
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import com.example.currencyconversion.utils.Constants
import com.example.currencyconversion.utils.NetworkConnectivity
import com.example.currencyconversion.utils.NetworkConnectivityImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import javax.inject.Inject

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ServerRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @IoDispatcher
    lateinit var testDispatcher: CoroutineDispatcher

    @Mock
    private lateinit var localDataRepository: LocalDataRepository

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var api: API

    @Mock
    private lateinit var networkConnectivity: NetworkConnectivity

    private lateinit var mockLocalDataRepository: ServerRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)

        Mockito.`when`(networkConnectivity.hasInternetConnection()).thenReturn(true)
        mockLocalDataRepository = ServerRepository(context, api, localDataRepository, testDispatcher, networkConnectivity)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_getServerExchangeRates_success() = runTest(testDispatcher) {
        val apiKey = "testApiKey"
        val rates = mapOf("USD" to 1.0, "EUR" to 0.85)

        val responseExchangeList = ResponseExchangeList("","","", rates,0)
        val response = Response.success(responseExchangeList)

        Mockito.`when`(api.getLatestData(apiKey)).thenReturn(response)

        val flow = mockLocalDataRepository.getServerExchangeRates(apiKey)

        val results = flow.toList()

        results.forEach { result ->
            println("Emitted result: $result")
        }

        assertTrue("No results emitted from the flow", results.isNotEmpty())

        val successResult = results.firstOrNull { it is NetworkResult.Success<ResponseExchangeList> } as? NetworkResult.Success<ResponseExchangeList>
        assertNotNull("No success result found", successResult)

        val data = successResult?._data
        assertNotNull("Success result data is null", data)
        assertEquals("Rates size mismatch", 2, data?.rates?.size)
        assertEquals("USD rate mismatch", 1.0, data?.rates?.get("USD")!!, 0.001)
        assertEquals("EUR rate mismatch", 0.85, data?.rates?.get("EUR")!!, 0.001)
    }

    @Test
    fun test_getServerExchangeCurrency_success() = runTest(testDispatcher) {
        val currencies = mapOf("USD" to "United States Dollar", "EUR" to "Euro")
        val response = Response.success(currencies)

        // Mock the API call
        Mockito.`when`(api.getCurrencies()).thenReturn(response)

        val flow = mockLocalDataRepository.getServerExchangeCurrency()
        val results = flow.toList()

        results.forEach { result ->
            println("Emitted result: $result")
        }

        // Assertions
        assertTrue("No results emitted from the flow", results.isNotEmpty())

        val successResult = results.firstOrNull { it is NetworkResult.Success<Map<String, String>> } as? NetworkResult.Success<Map<String, String>>
        assertNotNull("No success result found", successResult)

        val data = successResult?._data
        assertNotNull("Success result data is null", data)
        assertEquals("Currencies size mismatch", 2, data?.size)
        assertEquals("USD currency name mismatch", "United States Dollar", data?.get("USD"))
        assertEquals("EUR currency name mismatch", "Euro", data?.get("EUR"))
    }

    @Test
    fun test_getServerExchangeRates_noInternet() = runTest(testDispatcher) {
        val apiKey = "testApiKey"

        // Mock the network connectivity method to return false
        Mockito.`when`(networkConnectivity.hasInternetConnection()).thenReturn(false)

        val flow = mockLocalDataRepository.getServerExchangeRates(apiKey)
        val results = flow.toList()

        results.forEach { result ->
            println("Emitted result: $result")
        }

        // Assertions
        assertTrue("No results emitted from the flow", results.isNotEmpty())

        val errorResult = results.firstOrNull { it is NetworkResult.Error } as? NetworkResult.Error
        assertNotNull("No error result found", errorResult)

        val message = errorResult?.exception
        assertEquals("Error message mismatch", Constants.API_INTERNET_MESSAGE, message)
    }

    @Test
    fun test_getServerExchangeCurrency_noInternet() = runTest(testDispatcher) {

        // Mock the network connectivity method to return false
        Mockito.`when`(networkConnectivity.hasInternetConnection()).thenReturn(false)

        val flow = mockLocalDataRepository.getServerExchangeCurrency()
        val results = flow.toList()

        results.forEach { result ->
            println("Emitted result: $result")
        }

        // Assertions
        assertTrue("No results emitted from the flow", results.isNotEmpty())

        val errorResult = results.firstOrNull { it is NetworkResult.Error } as? NetworkResult.Error
        assertNotNull("No error result found", errorResult)

        val message = errorResult?.exception
        assertEquals("Error message mismatch", Constants.API_INTERNET_MESSAGE, message)
    }
}