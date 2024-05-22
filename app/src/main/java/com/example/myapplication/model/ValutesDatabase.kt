package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class ValutesDatabase(
    @SerializedName("Date") val date: String = "",
    @SerializedName("PreviousDate") val previousDate: String = "",
    @SerializedName("PreviousURL") val previousURL: String = "",
    @SerializedName("Timestamp") val timestamp: String = "",
    @SerializedName("Valute") val valute: Map<String, Valute> = emptyMap()
)