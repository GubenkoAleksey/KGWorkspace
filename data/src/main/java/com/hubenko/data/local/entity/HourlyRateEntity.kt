package com.hubenko.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_rates")
data class HourlyRateEntity(
    @PrimaryKey val id: String,
    val label: String,
    val value: Double
)
