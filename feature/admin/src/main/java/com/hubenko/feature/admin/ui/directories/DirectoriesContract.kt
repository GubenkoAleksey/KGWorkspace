package com.hubenko.feature.admin.ui.directories

import androidx.compose.runtime.Stable
import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.ViewIntent
import com.hubenko.core.presentation.ViewSideEffect
import com.hubenko.core.presentation.ViewState
import com.hubenko.feature.admin.ui.model.BaseRateUi
import com.hubenko.feature.admin.ui.model.HourlyRateUi
import com.hubenko.feature.admin.ui.model.RoleUi
import com.hubenko.feature.admin.ui.model.StatusTypeUi

enum class DirectorySection { StatusTypes, Roles, BaseRates, HourlyRates }

@Stable
data class DirectoriesState(
    val statusTypes: List<StatusTypeUi> = emptyList(),
    val roles: List<RoleUi> = emptyList(),
    val baseRates: List<BaseRateUi> = emptyList(),
    val hourlyRates: List<HourlyRateUi> = emptyList(),
    val isLoading: Boolean = false,
    val dialog: DirectoryDialog? = null,
    val expandedSections: Set<DirectorySection> = DirectorySection.entries.toSet()
) : ViewState

sealed interface DirectoryDialog {
    data class EditStatusType(val item: StatusTypeUi?) : DirectoryDialog
    data class EditRole(val item: RoleUi?) : DirectoryDialog
    data class EditBaseRate(val item: BaseRateUi?) : DirectoryDialog
    data class EditHourlyRate(val item: HourlyRateUi?) : DirectoryDialog
    data class ConfirmDeleteStatusType(val type: String, val label: String) : DirectoryDialog
    data class ConfirmDeleteRole(val id: String, val label: String) : DirectoryDialog
    data class ConfirmDeleteBaseRate(val id: String, val label: String) : DirectoryDialog
    data class ConfirmDeleteHourlyRate(val id: String, val label: String) : DirectoryDialog
}

sealed interface DirectoriesIntent : ViewIntent {
    data object OnAddStatusTypeClick : DirectoriesIntent
    data class OnEditStatusTypeClick(val item: StatusTypeUi) : DirectoriesIntent
    data class OnDeleteStatusTypeClick(val item: StatusTypeUi) : DirectoriesIntent
    data class OnSaveStatusType(val type: String, val label: String) : DirectoriesIntent
    data class OnConfirmDeleteStatusType(val type: String) : DirectoriesIntent

    data object OnAddRoleClick : DirectoriesIntent
    data class OnEditRoleClick(val item: RoleUi) : DirectoriesIntent
    data class OnDeleteRoleClick(val item: RoleUi) : DirectoriesIntent
    data class OnSaveRole(val id: String, val label: String) : DirectoriesIntent
    data class OnConfirmDeleteRole(val id: String) : DirectoriesIntent

    data object OnAddBaseRateClick : DirectoriesIntent
    data class OnEditBaseRateClick(val item: BaseRateUi) : DirectoriesIntent
    data class OnDeleteBaseRateClick(val item: BaseRateUi) : DirectoriesIntent
    data class OnSaveBaseRate(val id: String, val label: String, val value: Double) : DirectoriesIntent
    data class OnConfirmDeleteBaseRate(val id: String) : DirectoriesIntent

    data object OnAddHourlyRateClick : DirectoriesIntent
    data class OnEditHourlyRateClick(val item: HourlyRateUi) : DirectoriesIntent
    data class OnDeleteHourlyRateClick(val item: HourlyRateUi) : DirectoriesIntent
    data class OnSaveHourlyRate(val id: String, val label: String, val value: Double) : DirectoriesIntent
    data class OnConfirmDeleteHourlyRate(val id: String) : DirectoriesIntent

    data class OnToggleSection(val section: DirectorySection) : DirectoriesIntent
    data object OnDismissDialog : DirectoriesIntent
}

sealed interface DirectoriesEffect : ViewSideEffect {
    data class ShowSnackbar(val message: UiText) : DirectoriesEffect
}
