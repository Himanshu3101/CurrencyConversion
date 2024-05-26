package com.example.currencyconversion.data.models

class ResponseExchangeList(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Rates,
    val timestamp: Int
)