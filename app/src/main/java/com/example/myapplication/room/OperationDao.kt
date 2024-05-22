package com.example.myapplication.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.model.Operation
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {

    @Upsert
    suspend fun upsert(note: Operation)

    @Delete
    suspend fun deleteOperation(note: Operation)

    @Query("SELECT * FROM Operation ORDER BY date")
    fun getAllOperations(): Flow<List<Operation>>

    @Query("SELECT * FROM Operation WHERE date >= strftime('%s', 'now', '-7 days') ORDER BY date")
    fun getAllOperationsLast7Days(): Flow<List<Operation>>

    @Query("SELECT * FROM Operation WHERE date >= strftime('%s', 'now', '-30 days') ORDER BY date")
    fun getAllOperationsLast30Days(): Flow<List<Operation>>

    @Query("SELECT SUM(sum) FROM Operation WHERE date >= strftime('%s', 'now', '-7 days') AND sum > 0")
    fun getIncomeLast7Days(): Flow<Int>

    @Query("SELECT SUM(sum) FROM Operation WHERE date >= strftime('%s', 'now', '-7 days') AND sum < 0")
    fun getExpensesLast7Days(): Flow<Int>

    @Query("SELECT SUM(sum) FROM Operation WHERE date >= strftime('%s', 'now', '-30 days') AND sum > 0")
    fun getIncomeLast30Days(): Flow<Int>

    @Query("SELECT SUM(sum) FROM Operation WHERE date >= strftime('%s', 'now', '-30 days') AND sum < 0")
    fun getExpensesLast30Days(): Flow<Int>

    @Query("SELECT SUM(sum) FROM Operation WHERE sum > 0")
    fun getTotalIncome(): Flow<Int>

    @Query("SELECT SUM(sum) FROM Operation WHERE sum < 0")
    fun getTotalExpenses(): Flow<Int>

    @Query("SELECT * FROM Operation WHERE id = :id")
    fun getOperationById(id: Int?): Operation?
}