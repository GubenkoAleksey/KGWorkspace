package com.hubenko.feature.admin.ui.statuses

import android.net.Uri
import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.EmployeeStatus

data class StatusesState(
    val statuses: List<EmployeeStatus> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleteDialogOpen: Boolean = false
) : ViewState

sealed class StatusesIntent : ViewIntent {
    data object LoadData : StatusesIntent()
    data object OnExportClick : StatusesIntent()
    data object OnDeleteAllClick : StatusesIntent()
    data object OnConfirmDelete : StatusesIntent()
    data object OnDismissDialog : StatusesIntent()
}

sealed class StatusesEffect : ViewSideEffect {
    data class ShowToast(val message: String) : StatusesEffect()
    data class ShareFile(val uri: Uri) : StatusesEffect()
}

