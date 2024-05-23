package com.example.currencyconversion.data.models

import com.example.currencyconversion.data.models.Rates

class ResponseExchangeList(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Rates,
    val timestamp: Int
)