package com.example.currencyconversion.utils

import com.example.currencyconversion.network.server.NetworkResult
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResultDeserializer<T> : JsonDeserializer<NetworkResult<T>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): NetworkResult<T> {
        val jsonObject = json?.asJsonObject ?: throw JsonParseException("Invalid JSON")

        val status = jsonObject.get("status").asString
        val data: T? = context?.deserialize(jsonObject.get("data"), (typeOfT as ParameterizedType).actualTypeArguments[0])
        val message = jsonObject.get("message")?.asString

        return when (status) {
            "SUCCESS" -> NetworkResult.Success(data)
            "ERROR" -> NetworkResult.Error(message ?: "Unknown error")
            else -> throw JsonParseException("Unknown status: $status")
        }
    }
}