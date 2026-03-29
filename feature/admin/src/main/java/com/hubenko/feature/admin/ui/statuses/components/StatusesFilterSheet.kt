package com.hubenko.feature.admin.ui.statuses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hubenko.core.ui.theme.CoreTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusesFilterSheet(
    currentFrom: Long?,
    currentTo: Long?,
    onApply: (from: Long?, to: Long?) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = currentFrom,
        initialSelectedEndDateMillis = currentTo
    )

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
                TextButton(onClick = onClear) {
                    Text("Скинути")
                }
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
                    onApply(from, to)
                },
                enabled = dateRangePickerState.selectedStartDateMillis != null &&
                        dateRangePickerState.selectedEndDateMillis != null,
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
@Preview(showBackground = true)
@Composable
private fun StatusesFilterSheetPreview() {
    CoreTheme {
        StatusesFilterSheet(
            currentFrom = null,
            currentTo = null,
            onApply = { _, _ -> },
            onClear = {},
            onDismiss = {}
        )
    }
}
