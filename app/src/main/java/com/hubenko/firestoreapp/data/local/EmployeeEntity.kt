package com.hubenko.firestoreapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val phoneNumber: String,
    val role: String = "USER"
)
