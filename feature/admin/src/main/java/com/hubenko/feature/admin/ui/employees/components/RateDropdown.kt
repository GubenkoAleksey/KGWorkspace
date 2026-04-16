package com.hubenko.feature.admin.ui.employees.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.presentation.theme.CoreTheme

data class RateEntry(val id: String, val label: String, val value: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateDropdown(
    label: String,
    unit: String,
    items: List<RateEntry>,
    selectedId: String,
    customValueText: String,
    onCatalogSelected: (id: String, value: Double) -> Unit,
    onCustomValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCustom = selectedId.isEmpty()
    var expanded by remember { mutableStateOf(false) }

    val selectedEntry = items.find { it.id == selectedId }
    val catalogDisplayLabel = selectedEntry
        ?.let { "${it.label} — ${"%.2f".format(it.value)} $unit" }
        ?: ""

    Column(modifier = modifier) {
        Text(text = label)

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = !isCustom,
                onClick = {
                    val first = items.firstOrNull()
                    if (first != null) onCatalogSelected(first.id, first.value)
                }
            )
            Text(text = "Зі списку")
            RadioButton(
                selected = isCustom,
                onClick = { onCatalogSelected("", customValueText.toDoubleOrNull() ?: 0.0) }
            )
            Text(text = "Власне значення")
        }

        if (isCustom) {
            OutlinedTextField(
                value = customValueText,
                onValueChange = onCustomValueChange,
                label = { Text(label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = catalogDisplayLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(label) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { entry ->
                        DropdownMenuItem(
                            text = { Text("${entry.label} — ${"%.2f".format(entry.value)} $unit") },
                            onClick = {
                                onCatalogSelected(entry.id, entry.value)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Catalog mode")
@Composable
private fun RateDropdownCatalogPreview() {
    CoreTheme {
        RateDropdown(
            label = "Основна ставка",
            unit = "грн",
            items = listOf(
                RateEntry("1", "Мінімальна (7100 грн)", 7100.0),
                RateEntry("2", "Стандартна (12000 грн)", 12000.0)
            ),
            selectedId = "1",
            customValueText = "",
            onCatalogSelected = { _, _ -> },
            onCustomValueChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Custom mode")
@Composable
private fun RateDropdownCustomPreview() {
    CoreTheme {
        RateDropdown(
            label = "Основна ставка",
            unit = "грн",
            items = listOf(
                RateEntry("1", "Мінімальна (7100 грн)", 7100.0),
                RateEntry("2", "Стандартна (12000 грн)", 12000.0)
            ),
            selectedId = "",
            customValueText = "9500",
            onCatalogSelected = { _, _ -> },
            onCustomValueChange = {}
        )
    }
}
