package com.example.myapplication.viewmodel

import com.example.myapplication.model.Operation
import com.example.myapplication.room.OperationDatabase
import kotlinx.coroutines.flow.Flow

class Repository(private val db: OperationDatabase) {

    suspend fun upsert(operation: Operation) {
        db.dao.upsert(operation)
    }

    suspend fun deleteOperation(operation: Operation) {
        db.dao.deleteOperation(operation)
    }

    fun getAllOperations() = db.dao.getAllOperations()

    fun getAllOperationsLast7Days() = db.dao.getAllOperationsLast7Days()

    fun getAllOperationsLast30Days() = db.dao.getAllOperationsLast30Days()

    fun getIncomeLast7Days() : Flow<Int> = db.dao.getIncomeLast7Days()

    fun getIncomeLast30Days() : Flow<Int> = db.dao.getIncomeLast30Days()

    fun getExpensesLast7Days() : Flow<Int> = db.dao.getExpensesLast7Days()

    fun getExpensesLast30Days() : Flow<Int> = db.dao.getExpensesLast30Days()

    fun getTotalIncome() : Flow<Int> = db.dao.getTotalIncome()

    fun getTotalExpenses() : Flow<Int> = db.dao.getTotalExpenses()

    fun getOperationById(id:Int?) : Operation? = db.dao.getOperationById(id)
}