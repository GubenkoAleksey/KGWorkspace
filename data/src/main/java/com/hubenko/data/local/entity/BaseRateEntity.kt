package com.hubenko.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_rates")
data class BaseRateEntity(
    @PrimaryKey val id: String,
    val label: String,
    val value: Double
)
