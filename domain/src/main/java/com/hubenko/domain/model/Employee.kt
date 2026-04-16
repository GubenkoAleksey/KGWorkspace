package com.hubenko.domain.model

data class Employee(
    val id: String,
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val phoneNumber: String,
    val role: String,
    val email: String = "",
    val baseRateId: String = "",
    val baseRateValue: Double = 0.0,
    val hourlyRateId: String = "",
    val hourlyRateValue: Double = 0.0
)
