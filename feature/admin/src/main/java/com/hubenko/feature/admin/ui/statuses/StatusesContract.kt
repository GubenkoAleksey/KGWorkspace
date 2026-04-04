package com.hubenko.feature.admin.ui.statuses

import android.net.Uri
import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.EmployeeStatus
import com.hubenko.domain.model.StatusType

data class StatusesState(
    val statuses: List<EmployeeStatus> = emptyList(),
    val employeeGroups: List<EmployeeStatusesGroup> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val filterDateFrom: Long? = null,
    val filterDateTo: Long? = null,
    val filterEmployeeIds: Set<String> = emptySet(),
    val filterStatusTypes: Set<String> = emptySet(),
    val availableStatusTypes: List<StatusType> = emptyList(),
    val isFilterSheetOpen: Boolean = false
) : ViewState

data class EmployeeStatusesGroup(
    val employeeId: String,
    val employeeName: String,
    val statuses: List<EmployeeStatus>,
    val isExpanded: Boolean = false
)

sealed class StatusesIntent : ViewIntent {
    data object LoadData : StatusesIntent()
    data object OnExportClick : StatusesIntent()
    data object OnDeleteAllClick : StatusesIntent()
    data object OnConfirmDelete : StatusesIntent()
    data object OnDismissDialog : StatusesIntent()
    data class OnEmployeeExpandToggle(val employeeId: String) : StatusesIntent()
    data object OnFilterClick : StatusesIntent()
    data class OnApplyFilter(val from: Long?, val to: Long?, val employeeIds: Set<String>, val statusTypes: Set<String>) : StatusesIntent()
    data object OnClearFilter : StatusesIntent()
    data object OnDismissFilterSheet : StatusesIntent()
}

sealed class StatusesEffect : ViewSideEffect {
    data class ShowToast(val message: String) : StatusesEffect()
    data class ShareFile(val uri: Uri) : StatusesEffect()
}

