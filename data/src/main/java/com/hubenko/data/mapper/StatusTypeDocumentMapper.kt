package com.hubenko.data.mapper

import com.hubenko.data.local.entity.StatusTypeEntity
import com.hubenko.data.remote.document.StatusTypeDocument
import com.hubenko.domain.model.StatusType

fun StatusTypeDocument.toStatusType() = StatusType(type = type, label = label)

fun StatusTypeDocument.toStatusTypeEntity() = StatusTypeEntity(type = type, label = label)
