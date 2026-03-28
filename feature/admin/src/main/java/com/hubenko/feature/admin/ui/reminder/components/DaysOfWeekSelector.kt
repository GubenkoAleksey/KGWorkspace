package com.hubenko.feature.admin.ui.reminder.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaysOfWeekSelector(
    selectedDays: List<Int>,
    onDaysChanged: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysMap = mapOf(
        2 to "Пн",
        3 to "Вт",
        4 to "Ср",
        5 to "Чт",
        6 to "Пт",
        7 to "Сб",
        1 to "Нд"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Дні тижня",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysMap.forEach { (dayInt, label) ->
                val isSelected = selectedDays.contains(dayInt)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newDays = if (isSelected) {
                            selectedDays - dayInt
                        } else {
                            (selectedDays + dayInt).sorted()
                        }
                        onDaysChanged(newDays)
                    },
                    label = { Text(label) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DaysOfWeekSelectorPreview() {
    MaterialTheme {
        DaysOfWeekSelector(
            selectedDays = listOf(2, 3, 4, 5, 6),
            onDaysChanged = {}
        )
    }
}
