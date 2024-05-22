package com.example.myapplication.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.model.Valute
import com.example.myapplication.model.ValutesDatabase
import com.example.myapplication.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ValutesViewModel : ViewModel() {
    private val apiService = RetrofitInstance.api

    private val _valutes: MutableState<ValutesDatabase?> = mutableStateOf<ValutesDatabase?>(null)
    val valutes: State<ValutesDatabase?> = _valutes


    init {
        getOperations()
    }

    fun getOperations() {
        // Запуск корутины в области видимости ViewModel
        viewModelScope.launch {
            try {
                val response = apiService.getValutesDatabase()
                _valutes.value = response
                /* if (response.isSuccessful) {
                    Log.d("Success", response.body().toString())
                    _valutes.value = response.body()
                } else {
                    Log.e("Error (response)", response.errorBody().toString())
                } */
            } catch (e: Exception) {
                Log.e("Error (viewModel.catch)", e.toString())
                // Обработка ошибок сетевого запроса или преобразования данных
                // Здесь можно добавить логирование или показать пользовательское сообщение об ошибке
            }
        }
    }
}