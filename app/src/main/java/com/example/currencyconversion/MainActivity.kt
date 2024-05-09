package com.example.currencyconversion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.ViewModels.Data_VM
import com.example.currencyconversion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: Data_VM by viewModels()
    lateinit var activityMainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)


        getDefaultConversionData()
        observeConversionData()



    }

    private fun observeConversionData() {
        try {
            viewModel.response.observe(this, Observer { result ->
                when(result){
                    is NetworkResult.Loading -> {
                        activityMainBinding.progress.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        activityMainBinding.progress.visibility = View.GONE
                        activityMainBinding.textViewData.text = result._data?.rates.toString()
                    }
                    is NetworkResult.Error -> {
                        activityMainBinding.progress.visibility = View.GONE
                        activityMainBinding.textViewData.text = "Error: "   /*${result.message}*/
                    }
                }
            })
            /*viewModel.response.observe(this) { response ->
                val apiResultHandler = ApiResultHandler<ApiResponseData>(this@ProductListActivity,
                    onLoading = {
                        activityProductListBinding.progress.visibility = View.VISIBLE
                    },
                    onSuccess = { data ->
                        activityProductListBinding.progress.visibility = View.GONE
                        data?.Data?.marketList?.let { productListAdapter.setProducts(it) }
                        activityProductListBinding.swipeRefreshLayout.isRefreshing = false
                    },
                    onFailure = {
                        activityProductListBinding.progress.visibility = View.GONE
                    })
                apiResultHandler.handleApiResult(response)
            }*/
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    private fun getDefaultConversionData() {
        viewModel.getExchangeData(BuildConfig.API_KEY)
    }



}