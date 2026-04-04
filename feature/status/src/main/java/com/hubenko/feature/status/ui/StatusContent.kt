package com.hubenko.feature.status.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Domain
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.theme.*
import com.hubenko.feature.status.ui.components.NoteInputField
import com.hubenko.feature.status.ui.components.StatusCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusContent(
    state: StatusState,
    onIntent: (StatusIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Оновити статус",
                onBackClick = { onIntent(StatusIntent.OnBackClick) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
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
                            val (icon, color) = when (statusType.type) {
                                "Office" -> Icons.Rounded.Domain to StatusOfficeLight
                                "Remote" -> Icons.Rounded.WifiTethering to StatusRemoteLight
                                "Sick" -> Icons.Rounded.HealthAndSafety to StatusSickLight
                                else -> Icons.Rounded.Work to StatusOfficeLight
                            }
                            val title = if (isActive) "Закінчити роботу (${statusType.label})" else statusType.label
                            val description = when (statusType.type) {
                                "Office" -> "Працюю безпосередньо в офісі"
                                "Remote" -> "Працюю дистанційно (Home Office)"
                                "Sick" -> "Відсутній через стан здоров'я"
                                else -> ""
                            }
                            StatusCard(
                                title = title,
                                description = description,
                                icon = icon,
                                color = color,
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
