package com.example.currencyconversion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyconversion.Utils.Constants
import com.example.currencyconversion.Utils.NetworkResultDeserializer
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.ViewModels.Data_VM
import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.databinding.ActivityMainBinding
import com.example.currencyconversion.workManager.DataFetchWorker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: Data_VM by viewModels()
    lateinit var activityMainBinding: ActivityMainBinding

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { result ->
                val dataJson = result.getStringExtra("data")
                dataJson?.let { json ->
                    val gson = GsonBuilder()
                        .registerTypeAdapter(
                            object : TypeToken<NetworkResult<ResponseExchangeList>>() {}.type,
                            NetworkResultDeserializer()
                        )
                        .create()
                    val type = object : TypeToken<NetworkResult<ResponseExchangeList>>() {}.type
                    val data: NetworkResult<ResponseExchangeList> = gson.fromJson(json, type)
                    Log.e("MainActivityLog", "Receiver Value $data")
                    viewModel.setExchangeData(data)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        ObserverExchangeData()
        ObserveDBConversionData()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            dataUpdateReceiver,
            IntentFilter("com.example.UPDATE_DATA")
        )

        val checkData = activityMainBinding.textViewData.text
        Log.e("MainActivityLog", "before VM Method call $checkData")
        if (activityMainBinding.textViewData.text.equals("Hello World!")
            || activityMainBinding.textViewData.text.equals("")){
              //- Need to place outside
            viewModel.getDBConversionData().toString()
            Log.e("MainActivityLog", "after VM Method call")
        }

        activityMainBinding.clear.setOnClickListener {
            activityMainBinding.textViewData.text = ""
        }
    }

    private fun ObserverExchangeData() {
        viewModel.exchangeDataWorker.observe(this, Observer { result ->
            result.data?.let {
                activityMainBinding.textViewData.text = it.rates.toString()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataUpdateReceiver)
    }

    // For DB Observer
    private fun ObserveDBConversionData() {
        try {
            lifecycleScope.launch {
                viewModel.dBConversionData.collect { rates ->
                    Log.e("MainActivityLog", "Observer DB $rates.toString()")
                    val ratesText = rates.joinToString("\n") { it.toString() }
                    activityMainBinding.textViewData.text = ratesText
                }

            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }
}



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
