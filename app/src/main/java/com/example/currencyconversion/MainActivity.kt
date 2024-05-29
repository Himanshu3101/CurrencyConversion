package com.example.currencyconversion

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.currencyconversion.viewModels.Data_VM
import com.example.currencyconversion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: Data_VM by viewModels()
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var listOfCurrency: List<String?>
    var selectedCurrency: String = "Selected Currency"

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { result ->

                /*if (result.hasExtra("basedCurrency")) {
                    val dataJson = result.getStringExtra("basedCurrency")
                    dataJson?.let { json ->
                        Log.d("MainActivityLog", "Received Exchange Rates: $json")
                    }
                }*/



                if (result.hasExtra("currencyData")) {
                    Log.d("MainActivityLog", "Start MainActivity by Worker")
                    val jsonCurrencyData = result?.getStringArrayExtra("currencyData")
                    jsonCurrencyData?.let { currencyData ->
                        listOfCurrency = currencyData.toList()
                        spinnerAdapter(listOfCurrency)
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
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        selectedCurrency = parent?.getItemAtPosition(position).toString()
                        currencyCalculation(
                            activityMainBinding.editTextAmount.text.toString(),
                            selectedCurrency
                        )
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        activityMainBinding.editTextAmount.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                activityMainBinding.editTextAmount.setText("$")
            }

        activityMainBinding.editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currencyCalculation(s.toString(), selectedCurrency)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun currencyCalculation(amount: String, selectedCurrency: String) {
        Log.d("MainActivityLog", "Before $amount $selectedCurrency")
        if (amount.isNotEmpty() && amount != "$" && selectedCurrency != "Selected Currency") {
            var amt = amount.removeSuffix("$")
            // To do Calculation. Here.,
            lifecycleScope.launch {
                val baseRate = viewModel.getSelectedCurrencyRate("USD")
                val conversionRate = viewModel.getSelectedCurrencyRate(selectedCurrency)
                var result = (baseRate * conversionRate)
                Log.d("MainActivityLog", "Calc Complete $result")
            }
        }
    }

    private fun spinnerAdapter(currency: List<String?>) {
        val currencyList = listOf("Select Currency") + currency
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityMainBinding.spinnerCurrency.adapter = adapter
        activityMainBinding.spinnerCurrency.setSelection(0)
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
                viewModel.getDBConversionCurrency()
            }
        }
    }

    private fun ObserveDBConversionCurrency() {
        try {
            lifecycleScope.launch {
                viewModel.dBConversionCurrency.collect { currency ->
                    currency.let {
                        listOfCurrency = it.toList()
                        spinnerAdapter(listOfCurrency)
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
