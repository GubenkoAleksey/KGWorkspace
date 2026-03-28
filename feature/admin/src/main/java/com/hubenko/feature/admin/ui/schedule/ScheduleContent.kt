package com.hubenko.feature.admin.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.domain.model.Employee

/**
 * Stateless Composable для екрана розкладу сповіщень.
 * Відображає список співробітників для переходу до налаштувань нагадувань.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleContent(
    state: ScheduleState,
    onIntent: (ScheduleIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Розклад сповіщень",
                onBackClick = onBackClick
            )
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
                items(state.employees) { employee ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIntent(ScheduleIntent.OnEmployeeClick(employee.id)) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Text(
                                text = "${employee.lastName} ${employee.firstName}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
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
                    Employee("1", "Іванов", "Іван", "Іванович", "+380991234567", "USER"),
                    Employee("2", "Петренко", "Петро", "Петрович", "+380997654321", "ADMIN")
                )
            ),
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun ScheduleContentLoadingPreview() {
    CoreTheme {
        ScheduleContent(
            state = ScheduleState(isLoading = true),
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun ScheduleContentEmptyPreview() {
    CoreTheme {
        ScheduleContent(
            state = ScheduleState(),
            onIntent = {},
            onBackClick = {}
        )
    }
}

