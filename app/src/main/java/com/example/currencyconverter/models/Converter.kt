package com.example.currencyconverter.models

import com.google.gson.annotations.SerializedName

class Converter {
    @SerializedName("rates")
    lateinit var rates:Map<String,String>

}