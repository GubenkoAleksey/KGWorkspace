package com.hubenko.feature.admin.ui.directories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.components.AppTopBar
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText
import com.hubenko.feature.admin.R
import com.hubenko.feature.admin.ui.directories.components.DirectoryEntryDialog
import com.hubenko.feature.admin.ui.directories.components.DirectoryItemRow
import com.hubenko.feature.admin.ui.model.BaseRateUi
import com.hubenko.feature.admin.ui.model.HourlyRateUi
import com.hubenko.feature.admin.ui.model.RoleUi
import com.hubenko.feature.admin.ui.model.StatusTypeUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoriesContent(
    state: DirectoriesState,
    onIntent: (DirectoriesIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Довідники")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item(key = "header_status_types") {
                    DirectorySectionHeader(
                        title = "Типи статусів",
                        isExpanded = DirectorySection.StatusTypes in state.expandedSections,
                        onToggle = { onIntent(DirectoriesIntent.OnToggleSection(DirectorySection.StatusTypes)) },
                        onAdd = { onIntent(DirectoriesIntent.OnAddStatusTypeClick) }
                    )
                }
                item(key = "content_status_types") {
                    AnimatedVisibility(
                        visible = DirectorySection.StatusTypes in state.expandedSections,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeOut()
                    ) {
                        Column {
                            state.statusTypes.forEach { item ->
                                key(item.type) {
                                    DirectoryItemRow(
                                        label = item.label,
                                        keyValue = item.type,
                                        onEdit = {
                                            onIntent(
                                                DirectoriesIntent.OnEditStatusTypeClick(
                                                    item
                                                )
                                            )
                                        },
                                        onDelete = {
                                            onIntent(
                                                DirectoriesIntent.OnDeleteStatusTypeClick(
                                                    item
                                                )
                                            )
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }

                item(key = "spacer_roles") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item(key = "header_roles") {
                    DirectorySectionHeader(
                        title = "Ролі користувачів",
                        isExpanded = DirectorySection.Roles in state.expandedSections,
                        onToggle = { onIntent(DirectoriesIntent.OnToggleSection(DirectorySection.Roles)) },
                        onAdd = { onIntent(DirectoriesIntent.OnAddRoleClick) }
                    )
                }
                item(key = "content_roles") {
                    AnimatedVisibility(
                        visible = DirectorySection.Roles in state.expandedSections,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeOut()
                    ) {
                        Column {
                            state.roles.forEach { item ->
                                key(item.id) {
                                    DirectoryItemRow(
                                        label = item.label,
                                        keyValue = item.id,
                                        onEdit = { onIntent(DirectoriesIntent.OnEditRoleClick(item)) },
                                        onDelete = {
                                            onIntent(
                                                DirectoriesIntent.OnDeleteRoleClick(
                                                    item
                                                )
                                            )
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }

                item(key = "spacer_base_rates") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item(key = "header_base_rates") {
                    DirectorySectionHeader(
                        title = "Основна ставка",
                        isExpanded = DirectorySection.BaseRates in state.expandedSections,
                        onToggle = { onIntent(DirectoriesIntent.OnToggleSection(DirectorySection.BaseRates)) },
                        onAdd = { onIntent(DirectoriesIntent.OnAddBaseRateClick) }
                    )
                }
                item(key = "content_base_rates") {
                    AnimatedVisibility(
                        visible = DirectorySection.BaseRates in state.expandedSections,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeOut()
                    ) {
                        Column {
                            state.baseRates.forEach { item ->
                                key(item.id) {
                                    DirectoryItemRow(
                                        label = item.label,
                                        keyValue = item.id,
                                        value = "%.2f грн".format(item.value),
                                        onEdit = { onIntent(DirectoriesIntent.OnEditBaseRateClick(item)) },
                                        onDelete = { onIntent(DirectoriesIntent.OnDeleteBaseRateClick(item)) }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }

                item(key = "spacer_hourly_rates") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item(key = "header_hourly_rates") {
                    DirectorySectionHeader(
                        title = "Оплата за годину",
                        isExpanded = DirectorySection.HourlyRates in state.expandedSections,
                        onToggle = { onIntent(DirectoriesIntent.OnToggleSection(DirectorySection.HourlyRates)) },
                        onAdd = { onIntent(DirectoriesIntent.OnAddHourlyRateClick) }
                    )
                }
                item(key = "content_hourly_rates") {
                    AnimatedVisibility(
                        visible = DirectorySection.HourlyRates in state.expandedSections,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeOut()
                    ) {
                        Column {
                            state.hourlyRates.forEach { item ->
                                key(item.id) {
                                    DirectoryItemRow(
                                        label = item.label,
                                        keyValue = item.id,
                                        value = "%.2f грн/год".format(item.value),
                                        onEdit = { onIntent(DirectoriesIntent.OnEditHourlyRateClick(item)) },
                                        onDelete = { onIntent(DirectoriesIntent.OnDeleteHourlyRateClick(item)) }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    when (val dialog = state.dialog) {
        is DirectoryDialog.EditStatusType -> DirectoryEntryDialog(
            title = if (dialog.item == null) "Додати тип статусу" else "Редагувати тип статусу",
            keyLabel = "Ключ (тип)",
            labelLabel = "Назва",
            initialKey = dialog.item?.type ?: "",
            initialLabel = dialog.item?.label ?: "",
            isKeyEditable = dialog.item == null,
            onSave = { key, label, _ -> onIntent(DirectoriesIntent.OnSaveStatusType(key, label)) },
            onDismiss = { onIntent(DirectoriesIntent.OnDismissDialog) }
        )

        is DirectoryDialog.EditRole -> DirectoryEntryDialog(
            title = if (dialog.item == null) "Додати роль" else "Редагувати роль",
            keyLabel = "ID ролі",
            labelLabel = "Назва",
            initialKey = dialog.item?.id ?: "",
            initialLabel = dialog.item?.label ?: "",
            isKeyEditable = dialog.item == null,
            onSave = { key, label, _ -> onIntent(DirectoriesIntent.OnSaveRole(key, label)) },
            onDismiss = { onIntent(DirectoriesIntent.OnDismissDialog) }
        )

        is DirectoryDialog.ConfirmDeleteStatusType -> AlertDialog(
            onDismissRequest = { onIntent(DirectoriesIntent.OnDismissDialog) },
            title = { Text("Видалити тип статусу?") },
            text = { Text("«${dialog.label}» буде видалено з Firebase.") },
            confirmButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnConfirmDeleteStatusType(dialog.type)) }) {
                    Text("Видалити", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnDismissDialog) }) {
                    Text("Скасувати")
                }
            }
        )

        is DirectoryDialog.ConfirmDeleteRole -> AlertDialog(
            onDismissRequest = { onIntent(DirectoriesIntent.OnDismissDialog) },
            title = { Text("Видалити роль?") },
            text = { Text("«${dialog.label}» буде видалено з Firebase.") },
            confirmButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnConfirmDeleteRole(dialog.id)) }) {
                    Text("Видалити", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnDismissDialog) }) {
                    Text("Скасувати")
                }
            }
        )

        is DirectoryDialog.EditBaseRate -> DirectoryEntryDialog(
            title = if (dialog.item == null) "Додати ставку" else "Редагувати ставку",
            keyLabel = "ID ставки",
            labelLabel = "Назва",
            valueLabel = "Сума (грн)",
            initialKey = dialog.item?.id ?: "",
            initialLabel = dialog.item?.label ?: "",
            initialValue = dialog.item?.value?.let { "%.2f".format(it) } ?: "",
            isKeyEditable = dialog.item == null,
            onSave = { key, label, value ->
                onIntent(DirectoriesIntent.OnSaveBaseRate(key, label, value.toDoubleOrNull() ?: 0.0))
            },
            onDismiss = { onIntent(DirectoriesIntent.OnDismissDialog) }
        )

        is DirectoryDialog.ConfirmDeleteBaseRate -> AlertDialog(
            onDismissRequest = { onIntent(DirectoriesIntent.OnDismissDialog) },
            title = { Text("Видалити ставку?") },
            text = { Text("«${dialog.label}» буде видалено з Firebase.") },
            confirmButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnConfirmDeleteBaseRate(dialog.id)) }) {
                    Text("Видалити", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnDismissDialog) }) {
                    Text("Скасувати")
                }
            }
        )

        is DirectoryDialog.EditHourlyRate -> DirectoryEntryDialog(
            title = if (dialog.item == null) "Додати тариф" else "Редагувати тариф",
            keyLabel = "ID тарифу",
            labelLabel = "Назва",
            valueLabel = "Значення (грн/год)",
            initialKey = dialog.item?.id ?: "",
            initialLabel = dialog.item?.label ?: "",
            initialValue = dialog.item?.value?.let { "%.2f".format(it) } ?: "",
            isKeyEditable = dialog.item == null,
            onSave = { key, label, value ->
                onIntent(DirectoriesIntent.OnSaveHourlyRate(key, label, value.toDoubleOrNull() ?: 0.0))
            },
            onDismiss = { onIntent(DirectoriesIntent.OnDismissDialog) }
        )

        is DirectoryDialog.ConfirmDeleteHourlyRate -> AlertDialog(
            onDismissRequest = { onIntent(DirectoriesIntent.OnDismissDialog) },
            title = { Text("Видалити тариф?") },
            text = { Text("«${dialog.label}» буде видалено з Firebase.") },
            confirmButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnConfirmDeleteHourlyRate(dialog.id)) }) {
                    Text("Видалити", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(DirectoriesIntent.OnDismissDialog) }) {
                    Text("Скасувати")
                }
            }
        )

        null -> Unit
    }
}

@Composable
private fun DirectorySectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevron_rotation"
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondaryText(),
                modifier = Modifier.graphicsLayer { rotationZ = chevronRotation }
            )
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add),
                    tint = MaterialTheme.colorScheme.secondaryText()
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "With data")
@Composable
private fun DirectoriesContentPreview() {
    CoreTheme {
        DirectoriesContent(
            state = DirectoriesState(
                statusTypes = listOf(
                    StatusTypeUi("Office", "В офісі"),
                    StatusTypeUi("Remote", "Віддалено"),
                    StatusTypeUi("Sick", "Лікарняний")
                ),
                roles = listOf(
                    RoleUi("USER", "Працівник"),
                    RoleUi("ADMIN", "Адміністратор")
                ),
                baseRates = listOf(
                    BaseRateUi("FULL_TIME", "Повна ставка", 1.0),
                    BaseRateUi("HALF_TIME", "Половина ставки", 0.5)
                ),
                hourlyRates = listOf(
                    HourlyRateUi("RATE_50", "50 грн/год", 50.0),
                    HourlyRateUi("RATE_100", "100 грн/год", 100.0)
                )
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun DirectoriesContentLoadingPreview() {
    CoreTheme {
        DirectoriesContent(
            state = DirectoriesState(isLoading = true),
            onIntent = {}
        )
    }
}
