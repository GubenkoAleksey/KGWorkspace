package com.hubenko.data.mapper

import com.hubenko.data.remote.document.BaseRateDocument
import com.hubenko.domain.model.BaseRate

fun BaseRateDocument.toBaseRate() = BaseRate(id = id, label = label, value = value)
