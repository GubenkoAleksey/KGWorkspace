package com.hubenko.domain.usecase

import com.hubenko.domain.model.EmployeeStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * UseCase для генерації CSV-вмісту на основі списку статусів.
 * Повертає рядок у форматі CSV з заголовком та рядками даних.
 */
class ExportStatusesUseCase @Inject constructor() {

    operator fun invoke(statuses: List<EmployeeStatus>): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("ID;ПІБ;Статус;Початок;Кінець\n")
        statuses.forEach { status ->
            val start = sdf.format(Date(status.startTime))
            val end = status.endTime?.let { sdf.format(Date(it)) } ?: "-"
            val fullName = status.employeeFullName ?: "Невідомо"
            sb.append(
                "${escapeCsvField(status.employeeId)};" +
                "${escapeCsvField(fullName)};" +
                "${escapeCsvField(status.status)};" +
                "${escapeCsvField(start)};" +
                "${escapeCsvField(end)}\n"
            )
        }
        return sb.toString()
    }

    /**
     * Екранує значення поля CSV: якщо значення містить крапку з комою, лапки або перенос рядка —
     * загортає його у подвійні лапки та екранує внутрішні лапки.
     */
    private fun escapeCsvField(value: String): String {
        return if (value.contains(';') || value.contains('"') || value.contains('\n')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
