package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.Role

data class RoleUi(
    val id: String,
    val label: String
)

fun Role.toRoleUi() = RoleUi(id = id, label = label)
