package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.StatusType

data class StatusTypeUi(
    val type: String,
    val label: String,
    val isSystem: Boolean = false
)

fun StatusType.toStatusTypeUi() = StatusTypeUi(type = type, label = label, isSystem = isSystem)
