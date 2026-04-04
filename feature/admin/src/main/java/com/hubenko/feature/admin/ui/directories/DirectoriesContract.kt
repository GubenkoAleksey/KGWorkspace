package com.hubenko.feature.admin.ui.directories

import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.Role
import com.hubenko.domain.model.StatusType

data class DirectoriesState(
    val statusTypes: List<StatusType> = emptyList(),
    val roles: List<Role> = emptyList(),
    val isLoading: Boolean = false,
    val dialog: DirectoryDialog? = null
) : ViewState

sealed class DirectoryDialog {
    data class EditStatusType(val item: StatusType?) : DirectoryDialog()
    data class EditRole(val item: Role?) : DirectoryDialog()
    data class ConfirmDeleteStatusType(val type: String, val label: String) : DirectoryDialog()
    data class ConfirmDeleteRole(val id: String, val label: String) : DirectoryDialog()
}

sealed class DirectoriesIntent : ViewIntent {
    data object OnAddStatusTypeClick : DirectoriesIntent()
    data class OnEditStatusTypeClick(val item: StatusType) : DirectoriesIntent()
    data class OnDeleteStatusTypeClick(val item: StatusType) : DirectoriesIntent()
    data class OnSaveStatusType(val type: String, val label: String) : DirectoriesIntent()
    data class OnConfirmDeleteStatusType(val type: String) : DirectoriesIntent()

    data object OnAddRoleClick : DirectoriesIntent()
    data class OnEditRoleClick(val item: Role) : DirectoriesIntent()
    data class OnDeleteRoleClick(val item: Role) : DirectoriesIntent()
    data class OnSaveRole(val id: String, val label: String) : DirectoriesIntent()
    data class OnConfirmDeleteRole(val id: String) : DirectoriesIntent()

    data object OnDismissDialog : DirectoriesIntent()
}

sealed class DirectoriesEffect : ViewSideEffect {
    data class ShowToast(val message: String) : DirectoriesEffect()
}
