package com.hubenko.feature.admin.navigation

import kotlinx.serialization.Serializable

@Serializable
data object AdminRoute

@Serializable
data object RegisterEmployeeRoute

@Serializable
data class ReminderSettingsRoute(val employeeId: String)

@Serializable
data class EmployeeStatusesRoute(val employeeId: String, val showPayment: Boolean = true)
