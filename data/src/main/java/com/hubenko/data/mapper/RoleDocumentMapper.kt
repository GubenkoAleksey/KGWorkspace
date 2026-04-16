package com.hubenko.data.mapper

import com.hubenko.data.remote.document.RoleDocument
import com.hubenko.domain.model.Role

fun RoleDocument.toRole() = Role(id = id, label = label, isSystem = isSystem)
