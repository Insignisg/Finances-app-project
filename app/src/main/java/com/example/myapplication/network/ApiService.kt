package com.example.myapplication.network

import com.example.myapplication.model.ValutesDatabase
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("/daily_json.js")
    suspend fun getValutesDatabase() : ValutesDatabase
}


