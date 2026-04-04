package com.hubenko.feature.admin.ui.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.domain.model.ReminderSettings
import com.hubenko.feature.admin.ui.reminder.components.DaysOfWeekSelector
import com.hubenko.feature.admin.ui.reminder.components.ReminderSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsContent(
    state: ReminderSettingsState,
    onIntent: (ReminderSettingsIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Налаштування нагадувань") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { snackbarHost() }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val settings = state.settings

                ReminderSection(
                    title = "Ранкове нагадування",
                    enabled = settings.morningEnabled,
                    onEnabledChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(morningEnabled = it))) },
                    startTime = settings.morningStartTime,
                    onStartTimeChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(morningStartTime = it))) },
                    endTime = settings.morningEndTime,
                    onEndTimeChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(morningEndTime = it))) },
                    intervalMinutes = settings.morningIntervalMinutes,
                    onIntervalChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(morningIntervalMinutes = it))) }
                )

                ReminderSection(
                    title = "Вечірнє нагадування",
                    enabled = settings.eveningEnabled,
                    onEnabledChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(eveningEnabled = it))) },
                    startTime = settings.eveningStartTime,
                    onStartTimeChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(eveningStartTime = it))) },
                    endTime = settings.eveningEndTime,
                    onEndTimeChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(eveningEndTime = it))) },
                    intervalMinutes = settings.eveningIntervalMinutes,
                    onIntervalChange = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(eveningIntervalMinutes = it))) }
                )

                DaysOfWeekSelector(
                    selectedDays = settings.daysOfWeek,
                    onDaysChanged = { onIntent(ReminderSettingsIntent.UpdateSettings(settings.copy(daysOfWeek = it))) }
                )

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = { onIntent(ReminderSettingsIntent.SaveSettings) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Зберегти налаштування", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderSettingsContentPreview() {
    MaterialTheme {
        ReminderSettingsContent(
            state = ReminderSettingsState(
                settings = ReminderSettings(),
                isLoading = false
            ),
            onIntent = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderSettingsContentLoadingPreview() {
    MaterialTheme {
        ReminderSettingsContent(
            state = ReminderSettingsState(isLoading = true),
            onIntent = {},
            onBack = {}
        )
    }
}
