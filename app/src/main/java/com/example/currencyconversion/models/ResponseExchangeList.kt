package com.example.currencyconversion.models

class ResponseExchangeList(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Map<String, Double>,
    val timestamp: Int
)