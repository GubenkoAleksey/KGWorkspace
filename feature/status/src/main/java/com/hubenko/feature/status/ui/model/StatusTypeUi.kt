package com.hubenko.feature.status.ui.model

import com.hubenko.domain.model.StatusType

data class StatusTypeUi(
    val type: String,
    val label: String
)

fun StatusType.toStatusTypeUi() = StatusTypeUi(type = type, label = label)
