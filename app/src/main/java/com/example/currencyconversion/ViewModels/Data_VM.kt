package com.example.currencyconversion.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.BuildConfig
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.repository.LocalDataRepository
import com.example.currencyconversion.repository.ROOMRepository
import com.example.currencyconversion.repository.ServerDataRepository
import com.example.currencyconversion.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class Data_VM @Inject constructor(
    private val roomRepository: ROOMRepository
) : ViewModel() {

    private val _dBConversionData = MutableStateFlow<List<Rates>>(emptyList())
    val dBConversionData: StateFlow<List<Rates>> = _dBConversionData

    private val _exchangeDataWorker = MutableLiveData<NetworkResult<ResponseExchangeList>>()
    val exchangeDataWorker : LiveData<NetworkResult<ResponseExchangeList>> = _exchangeDataWorker

    fun setExchangeData(data: NetworkResult<ResponseExchangeList>) {
        Log.e("Data_VMLog", "For Set By Worker")
        _exchangeDataWorker.postValue(data)
    }

    fun getDBConversionData() {
        Log.e("Data_VMLog", "GetDB")
        viewModelScope.launch(Dispatchers.IO) {
            val rates = roomRepository.getAllRates()
            if(rates!=null){
                Log.e("Data_VMLog", "ready to In WithContext")
                withContext(Dispatchers.Main) {
                    _dBConversionData.value = emptyList()
                    _dBConversionData.value = listOf(rates)
                    Log.e("Data_VMLog", "In WithContext")
                }
            }
        }
    }
}


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
