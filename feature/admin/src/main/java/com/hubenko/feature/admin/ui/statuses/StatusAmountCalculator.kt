package com.hubenko.feature.admin.ui.statuses

import com.hubenko.feature.admin.ui.model.EmployeeStatusUi
import java.util.Calendar

fun calculateBilledAmount(
    startTime: Long,
    endTime: Long?,
    rate: Double,
    now: Long = System.currentTimeMillis()
): Double {
    if (rate <= 0.0) return 0.0
    val hours = (roundToNearestHour(endTime ?: now) - roundToNearestHour(startTime)) / 3_600_000.0
    return rate * maxOf(0.0, hours)
}

fun calculateMonths(statuses: List<EmployeeStatusUi>): Int {
    if (statuses.isEmpty()) return 1
    return statuses
        .map {
            val c = Calendar.getInstance().apply { timeInMillis = it.startTime }
            c.get(Calendar.YEAR) * 12 + c.get(Calendar.MONTH)
        }
        .toSet()
        .size
}

fun calculatePayment(totalAmount: Double, months: Int, baseRateValue: Double): Double =
    totalAmount - (months * baseRateValue)

private fun roundToNearestHour(ms: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = ms
        val minutes = get(Calendar.MINUTE)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (minutes >= 30) add(Calendar.HOUR_OF_DAY, 1)
    }.timeInMillis
