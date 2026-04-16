package com.hubenko.feature.admin.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTopBar
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.feature.admin.ui.schedule.components.ScheduleEmployeeItem

/**
 * Stateless Composable для екрана розкладу сповіщень.
 * Відображає список співробітників для переходу до налаштувань нагадувань.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleContent(
    state: ScheduleState,
    onIntent: (ScheduleIntent) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Розклад сповіщень")
        }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.employees, key = { it.id }) { employee ->
                    ScheduleEmployeeItem(
                        employee = employee,
                        onClick = { onIntent(ScheduleIntent.OnEmployeeClick(employee.id)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Content State")
@Composable
private fun ScheduleContentPreview() {
    CoreTheme {
        ScheduleContent(
            state = ScheduleState(
                employees = listOf(
                    ScheduleEmployeeUi(
                        id = "1",
                        fullName = "Іванов Іван Іванович",
                        morningEnabled = true,
                        morningStartTime = "07:30",
                        morningEndTime = "08:00",
                        eveningEnabled = true,
                        eveningStartTime = "17:30",
                        eveningEndTime = "18:00",
                        formattedDaysOfWeek = "Пн, Вт, Ср, Чт, Пт"
                    ),
                    ScheduleEmployeeUi(
                        id = "2",
                        fullName = "Петренко Петро Петрович",
                        morningEnabled = true,
                        morningStartTime = "08:00",
                        morningEndTime = "08:30",
                        eveningEnabled = false,
                        eveningStartTime = "17:00",
                        eveningEndTime = "17:30",
                        formattedDaysOfWeek = "Пн, Ср, Пт"
                    )
                )
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun ScheduleContentLoadingPreview() {
    CoreTheme {
        ScheduleContent(
            state = ScheduleState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun ScheduleContentEmptyPreview() {
    CoreTheme {
        ScheduleContent(
            state = ScheduleState(),
            onIntent = {}
        )
    }
}
