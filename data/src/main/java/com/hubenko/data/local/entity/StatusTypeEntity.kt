package com.hubenko.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_types")
data class StatusTypeEntity(
    @PrimaryKey val type: String,
    val label: String
)
