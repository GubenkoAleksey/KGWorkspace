package com.hubenko.feature.status.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Domain
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTopBar
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.status.ui.model.EmployeeStatusUi
import com.hubenko.feature.status.ui.model.StatusTypeUi
import com.hubenko.feature.status.ui.components.NoteInputField
import com.hubenko.feature.status.ui.components.StatusCard
import com.hubenko.feature.status.ui.components.SubmitConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusContent(
    state: StatusState,
    onIntent: (StatusIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    if (state.showConfirmDialog && state.pendingStatus != null) {
        val pendingLabel = state.statusTypes
            .find { it.type == state.pendingStatus }?.label ?: state.pendingStatus
        SubmitConfirmDialog(
            status = pendingLabel,
            onConfirm = { onIntent(StatusIntent.ConfirmSubmit) },
            onDismiss = { onIntent(StatusIntent.DismissConfirmDialog) }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(title = "Оновити статус")
        },
        snackbarHost = { snackbarHost() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val active = state.activeStatus

                    state.statusTypes.forEach { statusType ->
                        val isSick = statusType.type == "Sick"
                        val isActive = active?.status == statusType.type
                        val showCard = if (isSick) active == null else active == null || isActive

                        if (showCard) {
                            val icon = when (statusType.type) {
                                "Office" -> Icons.Rounded.Domain
                                "Remote" -> Icons.Rounded.WifiTethering
                                "Sick" -> Icons.Rounded.HealthAndSafety
                                else -> Icons.Rounded.Work
                            }
                            val title = if (isActive) "Закінчити роботу (${statusType.label})" else statusType.label
                            val description = when (statusType.type) {
                                "Office" -> "Працюю безпосередньо в офісі"
                                "Remote" -> "Працюю віддалено"
                                "Sick" -> "Відсутній через стан здоров'я"
                                else -> ""
                            }
                            StatusCard(
                                title = title,
                                description = description,
                                icon = icon,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onIntent(StatusIntent.SubmitStatusClick(statusType.type)) },
                                enabled = !state.isLoading
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = state.activeStatus == null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    NoteInputField(
                        note = state.note,
                        onNoteChange = { onIntent(StatusIntent.UpdateNote(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Status Content")
@Composable
private fun StatusContentPreview() {
    CoreTheme {
        StatusContent(
            state = StatusState(isLoading = false),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Status Loading")
@Composable
private fun StatusLoadingPreview() {
    CoreTheme {
        StatusContent(
            state = StatusState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Active Status")
@Composable
private fun StatusActivePreview() {
    CoreTheme {
        StatusContent(
            state = StatusState(
                isLoading = false,
                activeStatus = EmployeeStatusUi(
                    id = "1",
                    employeeId = "emp1",
                    employeeFullName = "Іванов Іван Іванович",
                    status = "Office",
                    note = null,
                    startTime = System.currentTimeMillis() - 3_600_000,
                    endTime = null,
                    isSynced = true
                ),
                statusTypes = listOf(
                    StatusTypeUi(type = "Office", label = "Офіс"),
                    StatusTypeUi(type = "Remote", label = "Дистанційно"),
                    StatusTypeUi(type = "Sick", label = "Лікарняний")
                )
            ),
            onIntent = {}
        )
    }
}
