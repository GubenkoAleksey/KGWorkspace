package com.hubenko.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EmployeeStatusWithDetails(
    @Embedded val status: EmployeeStatusEntity,
    @Relation(
        parentColumn = "employeeId",
        entityColumn = "id"
    )
    val employee: EmployeeEntity? // Зроблено nullable, щоб уникнути IllegalStateException
)
