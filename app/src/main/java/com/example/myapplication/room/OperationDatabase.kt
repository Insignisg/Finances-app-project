package com.example.myapplication.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.model.Operation

@Database(entities = [Operation::class], version = 1)
abstract class OperationDatabase : RoomDatabase() {
    abstract val dao: OperationDao
}