package com.example.currencyconversion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.currencyconversion.adapter.ConversionListAdapter
import com.example.currencyconversion.databinding.ActivityMainBinding
import com.example.currencyconversion.di.IoDispatcher
import com.example.currencyconversion.models.EndResult
import com.example.currencyconversion.screens.Home
import com.example.currencyconversion.utils.Constants
import com.example.currencyconversion.viewModels.DataVModel
import com.example.currencyconversion.workManager.DataFetchWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: DataVModel by viewModels()
    lateinit var activityMainBinding: ActivityMainBinding
    var listOfCurrency: List<String?> = emptyList()
    var selectedCurrency: String = "Selected Currency"

    lateinit var conversionListAdapter: ConversionListAdapter
    var resultList: ArrayList<EndResult> = ArrayList()


    @Inject
    @IoDispatcher
    lateinit var dispatcher: CoroutineDispatcher


    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { result ->
                if (result.hasExtra("currencyData")) {
                    val jsonCurrencyData = result.getStringArrayExtra("currencyData")
                    jsonCurrencyData?.let { currencyData ->
                        listOfCurrency = currencyData.toList()
                        spinnerAdapter(listOfCurrency)
                        checkingUI()

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

        Log.d("MainActivityLog", "MActivity ")
        if (Constants.switch_UI) {
            activityMainBinding.materialSwitch.isVisible = false
            setupComposeUI()
            Log.d("MainActivityLog", "MActivity If")
        } else {
            Log.d("MainActivityLog", "MActivity else")
//            setContentView(activityMainBinding.root)
            materialThemeGenerated()
        }
//        startDataFetchingService()

    }

    private fun setupComposeUI() {
        activityMainBinding.composeLayout.setContent {
            Home()
        }
    }

    private fun startDataFetchingService() {
        // Start a service or other async task to fetch data
        Intent(this, DataFetchWorker::class.java).also { intent ->
            startService(intent)
        }
    }

    private fun checkingUI() {
        if (Constants.switch_UI && listOfCurrency.isNotEmpty()) {
            activityMainBinding.composeLayout.setContent {
                Home()
            }
        } else {
            materialThemeGenerated()
        }
    }

    private fun materialThemeGenerated() {

        activityMainBinding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        conversionListAdapter = ConversionListAdapter(resultList)
        activityMainBinding.recyclerView.adapter = conversionListAdapter
        checkAndFetchData()
        observeDBConversionCurrency()


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
                currencyCalculation(s.toString(), selectedCurrency)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    //For DB Methods
    private fun checkAndFetchData() {
        lifecycleScope.launch {
            val res = viewModel.isCurrencyTableEmpty()
            if (!res) {
                Log.d("MainActivityLog", "check and fetch")
                viewModel.getDBConversionCurrency()
            }
        }
    }


    private fun observeDBConversionCurrency() {
        try {
            lifecycleScope.launch {
                viewModel.dBConversionCurrency.collect { currency ->
                    currency.let {
                        listOfCurrency = it.toList()
                        spinnerAdapter(listOfCurrency)
                        Log.d("MainActivityLog", "check and fetch Oberver")
                    }
                }
            }
        } catch (e: Exception) {
            e.stackTrace
            Log.e("MainActivityLog", "Error_" + e.message.toString())
        }
    }

    private fun spinnerAdapter(currency: List<String?>) {
        val currencyList = listOf("Select Currency") + currency
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityMainBinding.spinnerCurrency.adapter = adapter
        activityMainBinding.spinnerCurrency.setSelection(0)
    }

    fun currencyCalculation(inputAmt: String, selectedCurrency: String) {
        if (inputAmt.isNotEmpty() && selectedCurrency != "Selected Currency") {
            lifecycleScope.launch(dispatcher) {
                val baseRate =
                    viewModel.getSelectedCurrencyRate(selectedCurrency).toString().toDouble()

                val conversionResults = mutableListOf<EndResult>()

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
                    Log.d(
                        "MainActivityLog",
                        "Coversion $inputAmt $selectedCurrency is approximately $result $currency"
                    )
                    conversionResults.add(EndResult(result, currency))
                }
                withContext(Dispatchers.Main) {
                    resultList.clear()
                    resultList.addAll(conversionResults)
                    conversionListAdapter.notifyDataSetChanged()

                }

            }
        } else {
            Log.e("currencyCalculation", "Amount is null or blank")
            return
        }
    }


    private fun convertCurrency(amount: Double, sourceRate: Double, targetRate: Double): Double {
        return (amount / sourceRate) * targetRate
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataUpdateReceiver)
    }



}
