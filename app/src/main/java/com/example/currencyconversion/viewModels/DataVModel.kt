package com.example.currencyconversion.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.network.di.IoDispatcher
import com.example.currencyconversion.repository.ROOMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataVModel @Inject constructor(
    private val roomRepository: ROOMRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _dBConversionCurrency = MutableStateFlow<List<String?>>(emptyList())
    val dBConversionCurrency: StateFlow<List<String?>> = _dBConversionCurrency

    suspend fun isCurrencyTableEmpty(): Boolean {
        return roomRepository.isCurrencyTableEmpty()
    }

    fun getDBConversionCurrency() {
        viewModelScope.launch(dispatcher) {
            val currency = roomRepository.getAllCurrency()
            withContext(Dispatchers.Main) {
                _dBConversionCurrency.value = emptyList()
                _dBConversionCurrency.value = currency
            }
        }
    }

    suspend fun getSelectedCurrencyRate(currencyCountry: String?): Double? {
             return roomRepository.getAllRates(currencyCountry)
    }

}
