package com.example.currencyconversion.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.data.models.Rates
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.repository.LocalDataRepository
import com.example.currencyconversion.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Data_VM @Inject constructor(private val repository: ServerRepository, private val localDataRepository: LocalDataRepository) : ViewModel(){

    private val _response: MutableLiveData<NetworkResult<ResponseExchangeList>> = MutableLiveData()
    val response: LiveData<NetworkResult<ResponseExchangeList>> = _response

    fun getExchangeData(apiKey: String) = viewModelScope.launch {
        repository.getServerExchangeData( apiKey).collect { values: NetworkResult<ResponseExchangeList> ->
            _response.value = values
        }
    }

    fun DBExchangeData(rates: Rates) = viewModelScope.launch {
        localDataRepository.insertDBExchangeData(rates)
    }
}