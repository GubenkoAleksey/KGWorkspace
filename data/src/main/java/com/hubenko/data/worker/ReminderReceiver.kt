package com.hubenko.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hubenko.core.presentation.utils.NotificationHelper
import com.hubenko.domain.repository.ReminderRepository
import com.hubenko.domain.repository.StatusRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var statusRepository: StatusRepository
    @Inject lateinit var reminderRepository: ReminderRepository
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var alarmScheduler: AlarmScheduler

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "ReminderReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val employeeId = intent.getStringExtra("EMPLOYEE_ID") ?: return
        val type = intent.getStringExtra("REMINDER_TYPE") ?: return
        
        Log.d(TAG, "Received alarm for $employeeId, Type: $type")

        scope.launch {
            try {
                val settings = reminderRepository.getLocalSettings(employeeId)
                if (settings == null) {
                    Log.e(TAG, "Settings not found for employee: $employeeId")
                    return@launch
                }
                
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis

                val isSick = statusRepository.getSickStatusForToday(employeeId, startOfDay) != null
                if (isSick) {
                    Log.d(TAG, "Employee $employeeId is on sick leave. Skipping notification.")
                    if (type != "TEST") alarmScheduler.scheduleReminder(settings)
                    return@launch
                }

                if (type == "MORNING") {
                    val count = statusRepository.getStatusCountForToday(employeeId, startOfDay)
                    Log.d(TAG, "Morning check: Status count for today = $count")
                    if (count == 0) {
                        Log.d(TAG, "Showing MORNING notification")
                        notificationHelper.showReminderNotification(
                            "Початок роботи", 
                            "Ви ще не відправили статус про початок роботи!"
                        )
                    }
                } else if (type == "EVENING") {
                    val activeStatus = statusRepository.getActiveStatus(employeeId)
                    Log.d(TAG, "Evening check: Active status found = ${activeStatus != null}, EndTime = ${activeStatus?.endTime}")
                    
                    if (activeStatus != null && activeStatus.endTime == null) {
                        Log.d(TAG, "Showing EVENING notification")
                        notificationHelper.showReminderNotification(
                            "Завершення роботи", 
                            "Не забудьте завершити робочий день!"
                        )
                    } else {
                        Log.d(TAG, "No active status or it's already ended. Skipping notification.")
                    }
                } else if (type == "TEST") {
                    Log.d(TAG, "TEST Alarm triggered successfully!")
                    notificationHelper.showReminderNotification(
                        "ТЕСТ ТАЙМЕРА", 
                        "Таймер AlarmManager спрацював через 10 секунд!"
                    )
                }

                // Рекурсивно плануємо наступний запуск (через 5 хв або на вечір/ранок)
                if (type != "TEST") {
                    alarmScheduler.scheduleReminder(settings)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in onReceive for $employeeId: ${e.message}", e)
            }
        }
    }
}
