package com.example.currencyconversion.Utils

import com.example.currencyconversion.data.models.ResponseExchangeList
import com.example.currencyconversion.network.server.NetworkResult
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class NetworkResultDeserializer : JsonDeserializer<NetworkResult<ResponseExchangeList>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): NetworkResult<ResponseExchangeList> {
        val jsonObject = json?.asJsonObject ?: throw JsonParseException("Invalid JSON")

        val status = jsonObject.get("status").asString
        val data = context?.deserialize<ResponseExchangeList>(
            jsonObject.get("data"), ResponseExchangeList::class.java
        )
        val message = jsonObject.get("message")?.asString

        return when (status) {
            "SUCCESS" -> NetworkResult.Success(data)
            "ERROR" -> NetworkResult.Error(message ?: "Unknown error")
            else -> throw JsonParseException("Unknown status: $status")
        }
    }
}
