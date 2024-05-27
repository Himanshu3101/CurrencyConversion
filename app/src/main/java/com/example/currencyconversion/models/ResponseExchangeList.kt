package com.example.currencyconversion.models

class ResponseExchangeList(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Rates,
    val timestamp: Int
)