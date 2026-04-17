package com.hubenko.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "employee_hourly_rates",
    primaryKeys = ["employeeId", "statusType"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("employeeId")]
)
data class EmployeeHourlyRateEntity(
    val employeeId: String,
    val hourlyRateId: String,
    val hourlyRateValue: Double,
    val statusType: String
)
