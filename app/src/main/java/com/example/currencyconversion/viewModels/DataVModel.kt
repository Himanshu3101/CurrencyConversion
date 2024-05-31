package com.example.currencyconversion.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.repository.ROOMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataVModel @Inject constructor(
    private val roomRepository: ROOMRepository
) : ViewModel() {

    private val _dBConversionCurrency = MutableStateFlow<List<String?>>(emptyList())
    val dBConversionCurrency: StateFlow<List<String?>> = _dBConversionCurrency

    suspend fun isCurrencyTableEmpty(): Boolean {
        return roomRepository.isCurrencyTableEmpty()
    }

    fun getDBConversionCurrency() {
        Log.e("Data_VMLog", "GetDB")
        viewModelScope.launch(Dispatchers.IO) {
            val currency = roomRepository.getAllCurrency()
            Log.e("Data_VMLog", "ready to In WithContext")
            withContext(Dispatchers.Main) {
                _dBConversionCurrency.value = emptyList()
                _dBConversionCurrency.value = currency
                Log.e("Data_VMLog", "In WithContext")
            }
        }
    }

    suspend fun getSelectedCurrencyRate(currencyCountry: String?): Double? {
             return roomRepository.getAllRates(currencyCountry)
    }

}
