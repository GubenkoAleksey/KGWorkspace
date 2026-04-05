package com.hubenko.feature.admin.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.components.ProjectItem
import com.hubenko.core.ui.theme.CoreTheme
import com.hubenko.feature.admin.ui.AdminTab

/**
 * Stateless Composable головного дашборду адміністратора.
 * Відображає кнопки навігації до розділів панелі.
 *
 * @param onTabSelected Лямбда для вибору вкладки/розділу.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onTabSelected: (AdminTab) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Панель адміністратора",
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProjectItem(
                title = "Працівники",
                subtitle = "Керування працівниками",
                leadingIcon = Icons.Default.People,
                onClick = { onTabSelected(AdminTab.EMPLOYEES) }
            )
            ProjectItem(
                title = "Розклад",
                subtitle = "Налаштування графіка та нагадувань",
                leadingIcon = Icons.Default.Schedule,
                onClick = { onTabSelected(AdminTab.SCHEDULE) }
            )
            ProjectItem(
                title = "Статуси",
                subtitle = "Перегляд і контроль статусів",
                leadingIcon = Icons.Default.TaskAlt,
                onClick = { onTabSelected(AdminTab.STATUSES) }
            )
            ProjectItem(
                title = "Довідники",
                subtitle = "Редагування довідникових даних",
                leadingIcon = Icons.Default.Book,
                onClick = { onTabSelected(AdminTab.DIRECTORIES) }
            )
        }
    }
}


@Preview(showBackground = true, name = "Dashboard")
@Composable
private fun DashboardContentPreview() {
    CoreTheme {
        DashboardContent(
            isDarkTheme = false,
            onThemeToggle = {},
            onTabSelected = {}
        )
    }
}

