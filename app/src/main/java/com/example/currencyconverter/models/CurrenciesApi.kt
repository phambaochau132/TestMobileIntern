package com.example.currencyconverter.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

public interface CurrenciesApi {
    companion object{
        const val BASE_URL_SYMBOLS="https://api.exchangeratesapi.io/"
        const val BASE_URL_CONVERT="https://www.xe.com/api/"
    }
    @GET("v1/symbols?")
    fun getCurrencies (@Query("access_key")apiKey:String): Call<Currencies>

    @GET("protected/midmarket-converter/")
    fun convertCurrency (@Header("Authorization")apiKey:String): Call<Converter>
}