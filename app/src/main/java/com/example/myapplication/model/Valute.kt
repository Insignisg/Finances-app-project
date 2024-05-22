package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class Valute(
    @SerializedName("ID") val id: String = "",
    @SerializedName("NumCode") val numCode: String = "",
    @SerializedName("CharCode") val charCode: String = "",
    @SerializedName("Nominal") val nominal: Int = 0,
    @SerializedName("Name") val name: String = "",
    @SerializedName("Value") val value: Double = 0.0,
    @SerializedName("Previous") val previous: Double = 0.0
)
