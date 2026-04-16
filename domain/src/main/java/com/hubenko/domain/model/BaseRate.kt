package com.hubenko.domain.model

data class BaseRate(
    val id: String,
    val label: String,
    val value: Double,
    val isSystem: Boolean = false
)
