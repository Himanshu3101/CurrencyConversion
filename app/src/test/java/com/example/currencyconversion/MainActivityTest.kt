package com.example.currencyconversion

import androidx.lifecycle.ViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainActivityTest {

  /*  private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun currencyCalculation_blankInput_logsRequiredInput() = runTest {
        scenario.onActivity { activity ->
            val mockViewModel = mockk<ViewModel>(relaxed = true)
            activity.viewModel = mockViewModel

            val logSlot = slot<String>()
            mockkStatic(Log::class)
            every { Log.d(any(), capture(logSlot)) } answers { 0 }

            activity.currencyCalculation("", "")

            assertEquals("Amount and Currency both are required", logSlot.captured)
        }
    }*/
}




/*

class MainActivityTest {


    private val viewModel: ViewModel = mockk()
    private val listOfCurrency = listOf("USD", "EUR", "JPY", "GBP")

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testValidInputAndCurrency() = runTest {
        val inputAmt = "100"
        val selectedCurrency = "USD"
        every { viewModel.getSelectedCurrencyRate("USD") } returns 1.0
        every { viewModel.getSelectedCurrencyRate("EUR") } returns 0.85
        every { viewModel.getSelectedCurrencyRate("JPY") } returns 110.0
        every { viewModel.getSelectedCurrencyRate("GBP") } returns 0.75

        val result = mutableListOf<Pair<String, Double>>()

        coEvery {
            convertCurrency(any(), any(), any())
        } answers { callOriginal() }

        currencyCalculation(inputAmt, selectedCurrency)

        coVerify {
            convertCurrency(100.0, 1.0, 0.85)
            convertCurrency(100.0, 1.0, 110.0)
            convertCurrency(100.0, 1.0, 0.75)
        }

        // Further assert statements based on UI result handling.
    }

    @Test
    fun testEmptyInputAmount() = runTest {
        val inputAmt = ""
        val selectedCurrency = "USD"

        currencyCalculation(inputAmt, selectedCurrency)

        coVerify(exactly = 0) {
            viewModel.getSelectedCurrencyRate(any())
        }
    }

    @Test
    fun testSelectedCurrencyAsPlaceholder() = runTest {
        val inputAmt = "100"
        val selectedCurrency = "Selected Currency"

        currencyCalculation(inputAmt, selectedCurrency)

        coVerify(exactly = 0) {
            viewModel.getSelectedCurrencyRate(any())
        }
    }

    @Test
    fun testNullTargetRateHandling() = runTest {
        val inputAmt = "100"
        val selectedCurrency = "USD"
        every { viewModel.getSelectedCurrencyRate("USD") } returns 1.0
        every { viewModel.getSelectedCurrencyRate("EUR") } returns null
        every { viewModel.getSelectedCurrencyRate("JPY") } returns 110.0
        every { viewModel.getSelectedCurrencyRate("GBP") } returns null

        currencyCalculation(inputAmt, selectedCurrency)

        coVerify {
            convertCurrency(100.0, 1.0, 110.0)
        }

        // Further assert statements based on UI result handling.
    }

    private fun convertCurrency(amount: Double, baseRate: Double, targetRate: Double): Double {
        return (amount / baseRate) * targetRate
    }

    // Mock your lifecycleScope.launch for unit testing
    private fun currencyCalculation(inputAmt: String, selectedCurrency: String) {
        // The provided function's code
    }
}*/
