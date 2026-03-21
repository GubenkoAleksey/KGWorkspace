package com.hubenko.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee_status")
data class EmployeeStatusEntity(
    @PrimaryKey
    val id: String,
    val employeeId: String,
    val status: String,
    val note: String? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val isSynced: Boolean = false
)
