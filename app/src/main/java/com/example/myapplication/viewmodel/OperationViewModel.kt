package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Operation
import com.example.myapplication.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class OperationViewModel(private val repository: Repository) : ViewModel() {

    fun getOperations() = timePeriod.flatMapLatest { timePeriod ->
        if (timePeriod == "last week") {
            repository.getAllOperationsLast7Days()
        } else if (timePeriod == "last month") {
            repository.getAllOperationsLast30Days()
        } else {
            repository.getAllOperations()
        }
    }.asLiveData(viewModelScope.coroutineContext)

    fun getIncome() = timePeriod.flatMapLatest { timePeriod ->
        if (timePeriod == "last week") {
            repository.getIncomeLast7Days()
        } else if (timePeriod == "last month") {
            repository.getIncomeLast30Days()
        } else {
            repository.getTotalIncome()
        }
    }.asLiveData()

    fun getExpenses() = timePeriod.flatMapLatest { timePeriod ->
        if (timePeriod == "last week") {
            repository.getExpensesLast7Days()
        } else if (timePeriod == "last month") {
            repository.getExpensesLast30Days()
        } else {
            repository.getTotalExpenses()
        }
    }.asLiveData()

    /* fun getIncome() : Flow<Int>{
        if (timePeriod.value == "last week") {
            return repository.getIncomeLast7Days()
        } else if (timePeriod.value == "last month") {
            return repository.getIncomeLast30Days()
        } else {
            return repository.getTotalIncome()
        }
    } */

    private val timePeriod = MutableStateFlow("last week")

    fun upsert(operation: Operation) {
        viewModelScope.launch { repository.upsert(operation) }
    }

    fun delete(operation: Operation) {
        viewModelScope.launch { repository.deleteOperation(operation) }
    }

    fun getOperationById(id: Int?) : Operation? {
        return repository.getOperationById(id)
    }

    fun selectTimePeriod(period: String) {
        timePeriod.value = period
    }

}