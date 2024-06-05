package com.example.currencyconversion.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.di.IoDispatcher
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch(dispatcher) {
            val currency = localDataRepository.getAllCurrency()
            withContext(Dispatchers.Main) {
                _dBConversionCurrency.value = emptyList()
                _dBConversionCurrency.value = currency
            }
        }
    }

    open suspend fun getSelectedCurrencyRate(currencyCountry: String?): Double? {
//             return localDataRepository.getAllRates(currencyCountry)
        return withContext(dispatcher) {
            localDataRepository.getAllRates(currencyCountry)
        }
    }

}

//Attempt to invoke interface method 'java.lang.Object kotlin.coroutines.CoroutineContext.fold(java.lang.Object, kotlin.jvm.functions.Function2)' on a null object reference