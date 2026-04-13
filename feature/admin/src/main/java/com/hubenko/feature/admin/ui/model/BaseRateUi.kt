package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.BaseRate

data class BaseRateUi(
    val id: String,
    val label: String,
    val value: Double
)

fun BaseRate.toBaseRateUi() = BaseRateUi(id = id, label = label, value = value)
