package com.example.currencyconversion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.ViewModels.Data_VM
import com.example.currencyconversion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: Data_VM by viewModels()
    lateinit var activityMainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        observeServerConversionData()
        ObserveDBConversionData()

        activityMainBinding.clear.setOnClickListener {
            activityMainBinding.textViewData.text = ""
        }

        activityMainBinding.getDBData.setOnClickListener {
            viewModel.getDBConversionData()
        }

        activityMainBinding.getServerData.setOnClickListener {
            viewModel.getServerExchangeData(BuildConfig.API_KEY)

        }

//For Checking the INSERTION in DB
        /*viewModel.booleanValue.observe(this, Observer { value ->
            // Handle the boolean value change
            Toast.makeText(this, "Boolean value is: $value", Toast.LENGTH_SHORT).show()
        })*/
    }

    private fun ObserveDBConversionData() {
        try {
            lifecycleScope.launch {
                viewModel.dBConversionData.collect { rates ->
                    println("Collected rates: $rates")
                    val ratesText = rates.joinToString("\n") { it.toString() }
                    activityMainBinding.textViewData.text = ratesText
                }

            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }

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

                        result._data?.let {
                            viewModel.insertDBExchangeData(it.rates)
                        }
                    }

                    is NetworkResult.Error -> {
                        activityMainBinding.progress.visibility = View.GONE
                        activityMainBinding.textViewData.text = "Error:${result.message}"
                    }
                }
            })
        } catch (e: Exception) {
            e.stackTrace
        }
    }
}