package com.hubenko.feature.admin.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
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
    onTabSelected: (AdminTab) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Панель адміністратора")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DashboardButton(
                title = "Працівники",
                icon = Icons.Default.People,
                onClick = { onTabSelected(AdminTab.EMPLOYEES) }
            )
            DashboardButton(
                title = "Розклад",
                icon = Icons.Default.Schedule,
                onClick = { onTabSelected(AdminTab.SCHEDULE) }
            )
            DashboardButton(
                title = "Статуси",
                icon = Icons.Default.TaskAlt,
                onClick = { onTabSelected(AdminTab.STATUSES) }
            )
        }
    }
}

@Composable
private fun DashboardButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Dashboard")
@Composable
private fun DashboardContentPreview() {
    CoreTheme {
        DashboardContent(onTabSelected = {})
    }
}

