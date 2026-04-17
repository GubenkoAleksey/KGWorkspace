package com.hubenko.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EmployeeWithHourlyRates(
    @Embedded val employee: EmployeeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "employeeId"
    )
    val hourlyRates: List<EmployeeHourlyRateEntity>
)
