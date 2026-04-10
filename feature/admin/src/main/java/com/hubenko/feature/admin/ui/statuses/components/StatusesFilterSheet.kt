package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hubenko.core.presentation.theme.CoreTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatusesFilterSheet(
    currentFrom: Long?,
    currentTo: Long?,
    currentSelectedEmployeeIds: Set<String>,
    currentSelectedStatusTypes: Set<String>,
    employees: List<Pair<String, String>>,
    statusTypes: List<Pair<String, String>>,
    onApply: (from: Long?, to: Long?, employeeIds: Set<String>, statusTypes: Set<String>) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = currentFrom,
        initialSelectedEndDateMillis = currentTo
    )
    var selectedEmployeeIds by remember { mutableStateOf(currentSelectedEmployeeIds) }
    var selectedStatusTypes by remember { mutableStateOf(currentSelectedStatusTypes) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Фільтри",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(onClick = {
                    selectedEmployeeIds = emptySet()
                    selectedStatusTypes = emptySet()
                    onClear()
                }) {
                    Text("Скинути")
                }
            }

            if (statusTypes.isNotEmpty()) {
                Text(
                    text = "Тип статусу",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    statusTypes.forEach { (type, label) ->
                        val selected = type in selectedStatusTypes
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedStatusTypes = if (selected) {
                                    selectedStatusTypes - type
                                } else {
                                    selectedStatusTypes + type
                                }
                            },
                            label = { Text(label) }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            if (employees.isNotEmpty()) {
                Text(
                    text = "Працівники",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    employees.forEach { (id, name) ->
                        val selected = id in selectedEmployeeIds
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedEmployeeIds = if (selected) {
                                    selectedEmployeeIds - id
                                } else {
                                    selectedEmployeeIds + id
                                }
                            },
                            label = { Text(name) }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(max = 480.dp),
                showModeToggle = false,
                title = null,
                headline = {
                    MaterialTheme(
                        typography = MaterialTheme.typography.copy(
                            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontSize = 16.sp)
                        )
                    ) {
                        DateRangePickerDefaults.DateRangePickerHeadline(
                            selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                            selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                            displayMode = dateRangePickerState.displayMode,
                            dateFormatter = DatePickerDefaults.dateFormatter(),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            )

            Button(
                onClick = {
                    val from = dateRangePickerState.selectedStartDateMillis
                    val to = dateRangePickerState.selectedEndDateMillis?.plus(86_399_999L)
                    onApply(from, to, selectedEmployeeIds, selectedStatusTypes)
                },
                enabled = (dateRangePickerState.selectedStartDateMillis != null &&
                        dateRangePickerState.selectedEndDateMillis != null) ||
                        selectedEmployeeIds.isNotEmpty() ||
                        selectedStatusTypes.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Застосувати")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Empty")
@Composable
private fun StatusesFilterSheetEmptyPreview() {
    CoreTheme {
        StatusesFilterSheet(
            currentFrom = null,
            currentTo = null,
            currentSelectedEmployeeIds = emptySet(),
            currentSelectedStatusTypes = emptySet(),
            employees = emptyList(),
            statusTypes = emptyList(),
            onApply = { _, _, _, _ -> },
            onClear = {},
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "With filters")
@Composable
private fun StatusesFilterSheetWithFiltersPreview() {
    CoreTheme {
        StatusesFilterSheet(
            currentFrom = null,
            currentTo = null,
            currentSelectedEmployeeIds = setOf("emp_1"),
            currentSelectedStatusTypes = setOf("Office"),
            employees = listOf(
                "emp_1" to "Іванов Іван",
                "emp_2" to "Марія Коваль",
                "emp_3" to "Олег Сидоренко"
            ),
            statusTypes = listOf(
                "Office" to "Офіс",
                "Remote" to "Віддалено",
                "Sick" to "Лікарняний"
            ),
            onApply = { _, _, _, _ -> },
            onClear = {},
            onDismiss = {}
        )
    }
}
