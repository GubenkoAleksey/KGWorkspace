package com.hubenko.data.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.hubenko.domain.manager.ReminderManager
import com.hubenko.domain.model.ReminderSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderManager {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val TAG = "AlarmScheduler"
    private val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    override fun scheduleReminder(settings: ReminderSettings) {
        Log.d(TAG, "--- ScheduleReminder cycle started for: ${settings.employeeId} ---")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canSchedule = alarmManager.canScheduleExactAlarms()
            Log.d(TAG, "Can schedule exact alarms: $canSchedule")
        }

        cancelAllReminders(settings.employeeId)

        if (!settings.morningEnabled && !settings.eveningEnabled) {
            Log.d(TAG, "Reminders disabled in settings")
            return
        }

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        
        if (!settings.daysOfWeek.contains(currentDay)) {
            Log.d(TAG, "Today is not a working day. Scheduling for next valid day.")
            scheduleForNextValidDay(settings)
            return
        }

        val now = System.currentTimeMillis()
        val morningStart = getTimeInMillis(settings.morningStartTime)
        val morningEnd = getTimeInMillis(settings.morningEndTime)
        val eveningStart = getTimeInMillis(settings.eveningStartTime)
        val eveningEnd = getTimeInMillis(settings.eveningEndTime)

        Log.d(TAG, "Current Time: ${sdf.format(Date(now))}")
        Log.d(TAG, "Morning Window: ${settings.morningStartTime} - ${settings.morningEndTime}")
        Log.d(TAG, "Evening Window: ${settings.eveningStartTime} - ${settings.eveningEndTime}")

        when {
            settings.morningEnabled && now < morningEnd -> {
                val triggerTime = if (now < morningStart) morningStart else now + (settings.morningIntervalMinutes * 60 * 1000)
                if (triggerTime <= morningEnd) {
                    Log.d(TAG, ">>> Setting MORNING alarm for: ${sdf.format(Date(triggerTime))}")
                    setAlarm(triggerTime, settings.employeeId, "MORNING")
                } else if (settings.eveningEnabled) {
                    Log.d(TAG, "Morning window passed. Moving to Evening window start: ${settings.eveningStartTime}")
                    setAlarm(eveningStart, settings.employeeId, "EVENING")
                }
            }
            settings.eveningEnabled && now < eveningEnd -> {
                val triggerTime = if (now < eveningStart) eveningStart else now + (settings.eveningIntervalMinutes * 60 * 1000)
                if (triggerTime <= eveningEnd) {
                    Log.d(TAG, ">>> Setting EVENING alarm for: ${sdf.format(Date(triggerTime))}")
                    setAlarm(triggerTime, settings.employeeId, "EVENING")
                } else {
                    Log.d(TAG, "Evening window passed. Scheduling for tomorrow.")
                    scheduleForNextValidDay(settings)
                }
            }
            else -> {
                Log.d(TAG, "All windows for today passed. Scheduling for next day.")
                scheduleForNextValidDay(settings)
            }
        }
    }

    override fun scheduleTestAlarm(employeeId: String) {
        val triggerTime = System.currentTimeMillis() + 10000 // через 10 секунд
        Log.d(TAG, ">>> Setting TEST alarm for: ${sdf.format(Date(triggerTime))}")
        setAlarm(triggerTime, employeeId, "TEST")
    }

    private fun setAlarm(timeInMillis: Long, employeeId: String, type: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("EMPLOYEE_ID", employeeId)
            putExtra("REMINDER_TYPE", type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            employeeId.hashCode() + type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
            Log.d(TAG, "Alarm ($type) successfully set in System")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set alarm: ${e.message}")
        }
    }

    override fun cancelAllReminders(employeeId: String) {
        val types = listOf("MORNING", "EVENING", "TEST")
        types.forEach { type ->
            val intent = Intent(context, ReminderReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                context, employeeId.hashCode() + type.hashCode(), intent, 
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pi?.let { 
                alarmManager.cancel(it)
                Log.d(TAG, "Cancelled existing alarm for type: $type")
            }
        }
    }

    private fun getTimeInMillis(timeStr: String): Long {
        val parts = timeStr.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun scheduleForNextValidDay(settings: ReminderSettings) {
        val morningStart = getTimeInMillis(settings.morningStartTime) + (24 * 60 * 60 * 1000)
        setAlarm(morningStart, settings.employeeId, "MORNING")
    }
}
