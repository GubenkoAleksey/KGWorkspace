package com.hubenko.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey
    val id: String,
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val phoneNumber: String,
    val role: String,
    val email: String = ""
)
