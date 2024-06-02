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
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.currencyconversion.adapter.ConversionListAdapter
import com.example.currencyconversion.viewModels.DataVModel
import com.example.currencyconversion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: DataVModel by viewModels()
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var listOfCurrency: List<String?>
    var selectedCurrency: String = "Selected Currency"
    lateinit var conversionListAdapter: ConversionListAdapter

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { result ->
                if (result.hasExtra("currencyData")) {
                    val jsonCurrencyData = result.getStringArrayExtra("currencyData")
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


        ObserveDBConversionCurrency()
        checkAndFetchData()

        activityMainBinding.gridLayout.layoutManager = GridLayoutManager(this, 2)

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

        activityMainBinding.editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currencyCalculation(s.toString(), "INR")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


    }

    fun currencyCalculation(inputAmt: String, selectedCurrency: String) {
        // To do Calculation. Here.,
        if (inputAmt.isNotEmpty() && selectedCurrency != "Selected Currency") {
            lifecycleScope.launch(Dispatchers.IO) {
                val baseRate = viewModel.getSelectedCurrencyRate(selectedCurrency).toString().toDouble()

                val conversionResults = mutableListOf<Pair<String, Double?>>()

                for (currency in listOfCurrency) {
                    var targetRate = viewModel.getSelectedCurrencyRate(currency)
                    var result: Double? = null

                    if (targetRate == null) {
                        var currencySplit = currency?.let { currency.split(it.last()) }

                        for (splitCurrency in listOfCurrency) {
                            var currSplitList =
                                splitCurrency?.let { splitCurrency.split(it.last()) }

                            if (currencySplit != null) {
                                if (currencySplit.equals(currSplitList)) {


                                    if (currency != splitCurrency) {
                                        targetRate =
                                            viewModel.getSelectedCurrencyRate(splitCurrency)
                                        result = targetRate?.let {
                                            convertCurrency(inputAmt.toDouble(), baseRate, it)
                                        }
                                        break
                                    }


                                } else {
                                    Log.d(
                                        "MainActivityLog",
                                        "Don't have any approximate conversion using available exchange rates of related or newer versions of the currency"
                                    )
                                }
                            }
                        }
                    } else {
                        result = convertCurrency(inputAmt.toDouble(), baseRate, targetRate)
                    }

//                    conversionResults.add(currency to result)
//                }
//
//
//                withContext(Dispatchers.Main) {
//                    conversionListAdapter = ConversionListAdapter(conversionResults)
//                    activityMainBinding.gridLayout.adapter = conversionListAdapter
//                }


                    launch(Dispatchers.Main) {
                        //Result Handl For UI
                        Log.d(
                            "MainActivityLog",
                            "Coversion $inputAmt $selectedCurrency is approximately $result $currency"
                        )


//                            conversionListAdapter = ConversionListAdapter()
//                            activityMainBinding.gridLayout.adapter = conversionListAdapter

                    }
                }
            }
        }
    }

    fun convertCurrency(amount: Double, sourceRate: Double, targetRate: Double): Double {
        return (amount / sourceRate) * targetRate
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

