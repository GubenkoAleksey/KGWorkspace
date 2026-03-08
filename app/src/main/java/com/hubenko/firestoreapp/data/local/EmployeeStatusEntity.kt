package com.hubenko.firestoreapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee_status")
data class EmployeeStatusEntity(
    @PrimaryKey
    val id: String,
    val employeeId: String,
    val status: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
