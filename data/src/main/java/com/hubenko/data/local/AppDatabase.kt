package com.hubenko.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.dao.EmployeeStatusDao
import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.data.local.entity.EmployeeStatusEntity

@Database(
    entities = [EmployeeStatusEntity::class, EmployeeEntity::class],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeStatusDao(): EmployeeStatusDao
    abstract fun employeeDao(): EmployeeDao
}
