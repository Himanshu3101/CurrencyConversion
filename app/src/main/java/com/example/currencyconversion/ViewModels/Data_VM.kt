package com.example.currencyconversion.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.repository.ROOMRepository
import com.example.currencyconversion.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class Data_VM @Inject constructor(
    private val repository: ServerRepository,
    private val roomRepository: ROOMRepository
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<ResponseExchangeList>> = MutableLiveData()
    val response: LiveData<NetworkResult<ResponseExchangeList>> = _response

//For Checking the INSERTION in DB
    /* private val _booleanValue = MutableLiveData<Boolean>()
     val booleanValue: LiveData<Boolean> get() = _booleanValue*/

    private val _dBConversionData = MutableStateFlow<List<Rates>>(emptyList())
    val dBConversionData: StateFlow<List<Rates>> = _dBConversionData


    fun getServerExchangeData(apiKey: String) = viewModelScope.launch {
        repository.getServerExchangeData(apiKey)
            .collect { values: NetworkResult<ResponseExchangeList> ->
                _response.value = values
            }
    }

    fun insertDBExchangeData(rates: Rates) = viewModelScope.launch {
        //For Checking the INSERTION in DB
        /*_booleanValue.value = */ roomRepository.insertDBExchangeData(rates)
    }

    fun getDBConversionData() {
        viewModelScope.launch(Dispatchers.IO) {
            val rates = roomRepository.getAll()

            /*_dBConversionData.value = emptyList()
              delay(1)
            _dBConversionData.value = rates*/
//                  OR
            withContext(Dispatchers.Main) {
                _dBConversionData.value = emptyList()
                _dBConversionData.value = rates
                println("Updated StateFlow with rates")
            }
        }

        //This is for changes list when i want to get it changes by Flow or LiveData
        /*viewModelScope.launch {
            roomRepository.getAll()
                .flowOn(Dispatchers.IO)
                .catch { e -> e.printStackTrace() }
                .collect { rates: List<Rates> ->
                    println("Collected rates: $rates")
                    _dBConversionData.update { rates }
            }
        }*/
    }
}