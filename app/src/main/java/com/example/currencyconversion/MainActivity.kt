package com.example.currencyconversion

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.currencyconversion.utils.NetworkResultDeserializer
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.viewModels.Data_VM
import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: Data_VM by viewModels()
    lateinit var activityMainBinding: ActivityMainBinding

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { result ->

                /*if (result.hasExtra("data")) {
                    val dataJson = result.getStringExtra("data")
                    dataJson?.let { json ->
                        val gson = GsonBuilder()
                            .registerTypeAdapter(
                                object : TypeToken<NetworkResult<ResponseExchangeList>>() {}.type,
                                NetworkResultDeserializer<ResponseExchangeList>()
                            ).create()

                        val result: NetworkResult<ResponseExchangeList> = gson.fromJson(
                            json,
                            object : TypeToken<NetworkResult<ResponseExchangeList>>() {}.type
                        )
                        Log.d("MainActivityLog", "Received Exchange Rates: ${result.data?.rates}")
                        viewModel.setExchangeData(result)
                    }
                }*/
                if (result.hasExtra("currencyData")) {
                    val jsonCurrencyData = result?.getStringArrayExtra("currencyData")
                    jsonCurrencyData?.let { currencyData ->
                        val listOfStrings = currencyData.toList()
                        spinnerAdapter(listOfStrings)
                        Log.d("MainActivityLog", "Received Exchange Currency: $currencyData")
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            dataUpdateReceiver,
            IntentFilter("com.example.UPDATE_DATA")
        )

        viewModel.exchangeRatesWorker.observe(this, Observer { result ->
            result.data?.let {
//                activityMainBinding.textViewData.text = it.rates.toString()
            }
        })



        ObserveDBConversionCurrency()
        checkAndFetchData()

        activityMainBinding.spinnerCurrency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
//                val selectedItem = items[position]
//                Toast.makeText(this@MainActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }


    }

    private fun spinnerAdapter(currency: List<String?>) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, currency)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityMainBinding.spinnerCurrency.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataUpdateReceiver)
    }

    //For DB Methods
    private fun checkAndFetchData() {
        lifecycleScope.launch {
            val res = viewModel.isCurrencyTableEmpty()
            if (!res) {
                Log.d("MainActivityLog", "working with db")
                viewModel.getDBConversionCurrency()
            }
        }
    }

    private fun ObserveDBConversionCurrency() {
        try {
            lifecycleScope.launch {
                viewModel.dBConversionCurrency.collect { currency ->
                    currency.let {
                        Log.d("MainActivityLog", "getting from db ${it.toString()}")
                        spinnerAdapter(it)
                    }
                }

            }
        } catch (e: Exception) {
            e.stackTrace
            Log.e("MainActivityLog", "Error_" + e.message.toString())
        }
    }
}

/*viewModel.exchangeCurrencyWorker.observe(this, Observer { result ->
    result.let {
//                spinnerAdapter(it)
        activityMainBinding.textViewData.setText(it.toString())
    }
})*/


/*private fun ObserveDBConversionRates() {
    try {
        lifecycleScope.launch {
            viewModel.dBConversionRates.collect { rates ->
                rates.let {
                    activityMainBinding.textViewData.text =
                        rates.joinToString("\n") { it.toString() }
                }
//                    Log.e("MainActivityLog", "Observer DB $rates.toString()")
//                    val ratesText = rates.joinToString("\n") { it.toString() }
//                    activityMainBinding.textViewData.text = ratesText
            }

        }
    } catch (e: Exception) {
        e.stackTrace
    }
}*/


//For Checking the INSERTION in DB
/* viewModel.booleanValue.observe(this, Observer { value ->
     // Handle the boolean value change
     Toast.makeText(this, "Boolean value is: $value", Toast.LENGTH_SHORT).show()
 })*/


/*
private fun observeServerConversionData() {
    try {
        viewModel.response.observe(this, Observer { result ->

            when (result) {
                is NetworkResult.Loading -> {
                    activityMainBinding.progress.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    activityMainBinding.progress.visibility = View.GONE
                    activityMainBinding.textViewData.text = result._data?.rates?.toString()


                    //For ROOM DB
//                        result._data?.let {
//                            viewModel.insertDBExchangeData(it.rates)
//                        }
                }

                is NetworkResult.Error -> {
                    activityMainBinding.progress.visibility = View.GONE
                    activityMainBinding.textViewData.text = "Error:${result.message}"
                }
            }
        })
    } catch (e: Exception) {
        e.stackTrace
        e.message?.let { Log.d("Server Observer", it) }
    }
}*/
