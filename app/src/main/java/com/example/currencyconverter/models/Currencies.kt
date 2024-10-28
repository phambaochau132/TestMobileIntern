package com.example.currencyconverter.models

import com.google.gson.annotations.SerializedName

 class Currencies {
    @SerializedName("symbols")
    lateinit var symbols:Map<String,String>

}