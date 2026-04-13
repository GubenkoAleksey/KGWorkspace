package com.hubenko.feature.admin.ui.directories

import androidx.lifecycle.viewModelScope
import com.hubenko.core.presentation.BaseViewModel
import com.hubenko.core.presentation.toUiText
import com.hubenko.feature.admin.ui.model.BaseRateUi
import com.hubenko.feature.admin.ui.model.HourlyRateUi
import com.hubenko.feature.admin.ui.model.RoleUi
import com.hubenko.feature.admin.ui.model.StatusTypeUi
import com.hubenko.feature.admin.ui.model.toBaseRateUi
import com.hubenko.feature.admin.ui.model.toHourlyRateUi
import com.hubenko.feature.admin.ui.model.toRoleUi
import com.hubenko.domain.usecase.DeleteBaseRateUseCase
import com.hubenko.domain.usecase.DeleteHourlyRateUseCase
import com.hubenko.domain.usecase.DeleteRoleUseCase
import com.hubenko.domain.usecase.DeleteStatusTypeUseCase
import com.hubenko.domain.usecase.GetBaseRatesUseCase
import com.hubenko.domain.usecase.GetHourlyRatesUseCase
import com.hubenko.domain.usecase.GetRolesUseCase
import com.hubenko.domain.usecase.GetStatusTypesUseCase
import com.hubenko.domain.usecase.SaveBaseRateUseCase
import com.hubenko.domain.usecase.SaveHourlyRateUseCase
import com.hubenko.domain.usecase.SaveRoleUseCase
import com.hubenko.domain.usecase.SaveStatusTypeUseCase
import com.hubenko.domain.util.onFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DirectoriesViewModel @Inject constructor(
    private val getStatusTypesUseCase: GetStatusTypesUseCase,
    private val saveStatusTypeUseCase: SaveStatusTypeUseCase,
    private val deleteStatusTypeUseCase: DeleteStatusTypeUseCase,
    private val getRolesUseCase: GetRolesUseCase,
    private val saveRoleUseCase: SaveRoleUseCase,
    private val deleteRoleUseCase: DeleteRoleUseCase,
    private val getBaseRatesUseCase: GetBaseRatesUseCase,
    private val saveBaseRateUseCase: SaveBaseRateUseCase,
    private val deleteBaseRateUseCase: DeleteBaseRateUseCase,
    private val getHourlyRatesUseCase: GetHourlyRatesUseCase,
    private val saveHourlyRateUseCase: SaveHourlyRateUseCase,
    private val deleteHourlyRateUseCase: DeleteHourlyRateUseCase
) : BaseViewModel<DirectoriesState, DirectoriesIntent, DirectoriesEffect>(DirectoriesState()) {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getStatusTypesUseCase().collectLatest { types ->
                updateState { copy(statusTypes = types.map { StatusTypeUi(it.type, it.label) }) }
            }
        }
        viewModelScope.launch {
            getRolesUseCase().collectLatest { roles ->
                updateState { copy(roles = roles.map { it.toRoleUi() }) }
            }
        }
        viewModelScope.launch {
            getBaseRatesUseCase().collectLatest { rates ->
                updateState { copy(baseRates = rates.map { it.toBaseRateUi() }) }
            }
        }
        viewModelScope.launch {
            getHourlyRatesUseCase().collectLatest { rates ->
                updateState { copy(hourlyRates = rates.map { it.toHourlyRateUi() }) }
            }
        }
    }

    override fun onIntent(intent: DirectoriesIntent) {
        when (intent) {
            is DirectoriesIntent.OnAddStatusTypeClick ->
                updateState { copy(dialog = DirectoryDialog.EditStatusType(null)) }

            is DirectoriesIntent.OnEditStatusTypeClick ->
                updateState { copy(dialog = DirectoryDialog.EditStatusType(intent.item)) }

            is DirectoriesIntent.OnDeleteStatusTypeClick ->
                updateState { copy(dialog = DirectoryDialog.ConfirmDeleteStatusType(intent.item.type, intent.item.label)) }

            is DirectoriesIntent.OnSaveStatusType -> saveStatusType(intent.type, intent.label)

            is DirectoriesIntent.OnConfirmDeleteStatusType -> deleteStatusType(intent.type)

            is DirectoriesIntent.OnAddRoleClick ->
                updateState { copy(dialog = DirectoryDialog.EditRole(null)) }

            is DirectoriesIntent.OnEditRoleClick ->
                updateState { copy(dialog = DirectoryDialog.EditRole(intent.item)) }

            is DirectoriesIntent.OnDeleteRoleClick ->
                updateState { copy(dialog = DirectoryDialog.ConfirmDeleteRole(intent.item.id, intent.item.label)) }

            is DirectoriesIntent.OnSaveRole -> saveRole(intent.id, intent.label)

            is DirectoriesIntent.OnConfirmDeleteRole -> deleteRole(intent.id)

            is DirectoriesIntent.OnAddBaseRateClick ->
                updateState { copy(dialog = DirectoryDialog.EditBaseRate(null)) }

            is DirectoriesIntent.OnEditBaseRateClick ->
                updateState { copy(dialog = DirectoryDialog.EditBaseRate(intent.item)) }

            is DirectoriesIntent.OnDeleteBaseRateClick ->
                updateState { copy(dialog = DirectoryDialog.ConfirmDeleteBaseRate(intent.item.id, intent.item.label)) }

            is DirectoriesIntent.OnSaveBaseRate -> saveBaseRate(intent.id, intent.label, intent.value)

            is DirectoriesIntent.OnConfirmDeleteBaseRate -> deleteBaseRate(intent.id)

            is DirectoriesIntent.OnAddHourlyRateClick ->
                updateState { copy(dialog = DirectoryDialog.EditHourlyRate(null)) }

            is DirectoriesIntent.OnEditHourlyRateClick ->
                updateState { copy(dialog = DirectoryDialog.EditHourlyRate(intent.item)) }

            is DirectoriesIntent.OnDeleteHourlyRateClick ->
                updateState { copy(dialog = DirectoryDialog.ConfirmDeleteHourlyRate(intent.item.id, intent.item.label)) }

            is DirectoriesIntent.OnSaveHourlyRate -> saveHourlyRate(intent.id, intent.label, intent.value)

            is DirectoriesIntent.OnConfirmDeleteHourlyRate -> deleteHourlyRate(intent.id)

            is DirectoriesIntent.OnToggleSection -> updateState {
                val updated = if (intent.section in expandedSections)
                    expandedSections - intent.section
                else
                    expandedSections + intent.section
                copy(expandedSections = updated)
            }

            is DirectoriesIntent.OnDismissDialog ->
                updateState { copy(dialog = null) }
        }
    }

    private fun saveStatusType(type: String, label: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            saveStatusTypeUseCase(type, label)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }

    private fun deleteStatusType(type: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            deleteStatusTypeUseCase(type)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }

    private fun saveRole(id: String, label: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            saveRoleUseCase(id, label)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }

    private fun deleteRole(id: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            deleteRoleUseCase(id)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }

    private fun saveBaseRate(id: String, label: String, value: Double) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            saveBaseRateUseCase(id, label, value)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }

    private fun deleteBaseRate(id: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            deleteBaseRateUseCase(id)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }

    private fun saveHourlyRate(id: String, label: String, value: Double) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            saveHourlyRateUseCase(id, label, value)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }

    private fun deleteHourlyRate(id: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, dialog = null) }
            deleteHourlyRateUseCase(id)
                .onFailure { sendEffect(DirectoriesEffect.ShowSnackbar(it.toUiText())) }
            updateState { copy(isLoading = false) }
        }
    }
}
