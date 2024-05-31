package com.example.currencyconversion.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.repository.ROOMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class Data_VM @Inject constructor(
    private val roomRepository: ROOMRepository
) : ViewModel() {

    private val _dBConversionRates = MutableStateFlow<List<Rates>>(emptyList())
    val dBConversionRates: StateFlow<List<Rates>> = _dBConversionRates

    private val _dBConversionCurrency = MutableStateFlow<List<String?>>(emptyList())
    val dBConversionCurrency: StateFlow<List<String?>> = _dBConversionCurrency

    private val _exchangeRatesWorker = MutableLiveData<NetworkResult<ResponseExchangeList>>()
    val exchangeRatesWorker : LiveData<NetworkResult<ResponseExchangeList>> = _exchangeRatesWorker

    fun setExchangeData(data: NetworkResult<ResponseExchangeList>) {
        Log.e("Data_VMLog", "For Rates Set By Worker")
        _exchangeRatesWorker.postValue(data)
    }

    suspend fun isCurrencyTableEmpty(): Boolean {
        return roomRepository.isCurrencyTableEmpty()
    }

    /*fun getDBConversionRates() {
        Log.e("Data_VMLog", "GetDB")
        viewModelScope.launch(Dispatchers.IO) {
            val rates = roomRepository.getAllRates()
            Log.e("Data_VMLog", "ready to In WithContext")
            withContext(Dispatchers.Main) {
                _dBConversionRates.value = emptyList()
                _dBConversionRates.value = listOf(rates)
                Log.e("Data_VMLog", "In WithContext")
            }
        }
    }*/

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

/*    private val _exchangeCurrencyWorker = MutableLiveData<String?>()
    val exchangeCurrencyWorker : LiveData<String?> = _exchangeCurrencyWorker*/

/* fun setExchangeCurrency(result: String?) {
        Log.e("Data_VMLog", "For Currency Set By Worker")
        _exchangeCurrencyWorker.postValue(result)
    }*/


// Optionally: Fetch data directly within ViewModel if required
/* fun fetchExchangeData(apiKey: String) {
     viewModelScope.launch {
         repository.getServerExchangeData(apiKey).collect { result ->
             _exchangeDataWorker.postValue(result)
         }
     }
 }*/



/*private val _response: MutableLiveData<NetworkResult<ResponseExchangeList>> = MutableLiveData()
val response: LiveData<NetworkResult<ResponseExchangeList>> = _response*/
/*fun getServerExchangeData(apiKey: String) = viewModelScope.launch {
    repository.getServerExchangeData(apiKey)
        .collect { values: NetworkResult<ResponseExchangeList> ->
            _response.value = values
        }
}*/

//FOr ROOM DB
//    For Checking the INSERTION in DB
/*private val _booleanValue = MutableLiveData<Boolean>()
val booleanValue: LiveData<Boolean> get() = _booleanValue


fun insertDBExchangeData(rates: Rates) = viewModelScope.launch {
    //For Checking the INSERTION in DB
    _booleanValue.value =  roomRepository.insertDBExchangeData(rates)
}*/

/* getDBConversionData()
_dBConversionData.value = emptyList()
         delay(1)
       _dBConversionData.value = listOf(rates)*/




        //This is for changes list when i want to get it changes by Flow or LiveData
        /* viewModelScope.launch {
            roomRepository.getAll()
                .flowOn(Dispatchers.IO)
                .catch { e -> e.printStackTrace() }
                .collect { rates: List<Rates> ->
                    println("Collected rates: $rates")
                    _dBConversionData.update { rates }
            }
        }*/
