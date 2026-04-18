package com.hubenko.feature.admin.ui.statuses

import android.net.Uri
import androidx.compose.runtime.Stable
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState
import com.hubenko.feature.admin.ui.model.EmployeeStatusUi
import com.hubenko.feature.admin.ui.model.StatusTypeUi

@Stable
data class StatusesState(
    val statuses: List<EmployeeStatusUi> = emptyList(),
    val employeeGroups: List<EmployeeStatusesGroup> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleteDialogOpen: Boolean = false,
    val filterDateFrom: Long? = null,
    val filterDateTo: Long? = null,
    val filterEmployeeIds: Set<String> = emptySet(),
    val filterStatusTypes: Set<String> = emptySet(),
    val availableStatusTypes: List<StatusTypeUi> = emptyList(),
    val isFilterSheetOpen: Boolean = false,
    val showPayment: Boolean = true,
    val editingStatus: EmployeeStatusUi? = null,
    val deletingStatusId: String? = null
) : ViewState

data class EmployeeStatusesGroup(
    val employeeId: String,
    val employeeName: String,
    val statuses: List<EmployeeStatusUi>,
    val hourlyRates: Map<String, Double> = emptyMap(),
    val baseRateValue: Double = 0.0,
    val isExpanded: Boolean = false
)

sealed interface StatusesIntent : ViewIntent {
    data object LoadData : StatusesIntent
    data object OnExportClick : StatusesIntent
    data object OnDeleteAllClick : StatusesIntent
    data object OnConfirmDelete : StatusesIntent
    data object OnDismissDialog : StatusesIntent
    data class OnEmployeeExpandToggle(val employeeId: String) : StatusesIntent
    data object OnFilterClick : StatusesIntent
    data class OnApplyFilter(
        val from: Long?,
        val to: Long?,
        val employeeIds: Set<String>,
        val statusTypes: Set<String>
    ) : StatusesIntent
    data object OnClearFilter : StatusesIntent
    data object OnDismissFilterSheet : StatusesIntent
    data class OnStatusEditClick(val status: EmployeeStatusUi) : StatusesIntent
    data object OnEditStatusDismiss : StatusesIntent
    data class OnEditStatusSave(
        val id: String,
        val statusType: String,
        val startTime: Long,
        val endTime: Long?
    ) : StatusesIntent
    data class OnStatusDeleteClick(val statusId: String) : StatusesIntent
    data object OnConfirmDeleteStatus : StatusesIntent
    data object OnDismissDeleteStatus : StatusesIntent
}

sealed interface StatusesEffect : ViewSideEffect {
    data class ShowSnackbar(val message: UiText) : StatusesEffect
    data class ShareFile(val uri: Uri) : StatusesEffect
}
