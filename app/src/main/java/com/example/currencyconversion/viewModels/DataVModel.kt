package com.example.currencyconversion.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.currencyconversion.di.IoDispatcher
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class DataVModel @Inject constructor(
    private val localDataRepository: LocalDataRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _dBConversionCurrency = MutableStateFlow<List<String?>>(emptyList())
    val dBConversionCurrency: StateFlow<List<String?>> = _dBConversionCurrency


    suspend fun isCurrencyTableEmpty(): Boolean {
        return localDataRepository.isCurrencyTableEmpty() ?: true
    }

    open suspend fun getDBConversionCurrency() {
        val currency = withContext(dispatcher) {
            localDataRepository.getAllCurrency()
        }
        _dBConversionCurrency.value = emptyList()
        _dBConversionCurrency.value = currency
        Log.d("DataVModel", "getDBConversionCurrency")
    }

    open suspend fun getSelectedCurrencyRate(currencyCountry: String?): Double? {
        return withContext(dispatcher) {
            localDataRepository.getAllRates(currencyCountry)
        }
    }

}
