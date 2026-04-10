package com.hubenko.core.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val TAG = "NotificationHelper"

    companion object {
        const val REMINDER_CHANNEL_ID = "work_reminder_channel"
        const val REMINDER_NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Нагадування про статус"
            val descriptionText = "Сповіщення про необхідність відправити статус"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(REMINDER_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $REMINDER_CHANNEL_ID")
        }
    }

    fun showReminderNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "Notification permission not granted. Cannot show: $title")
                return
            }
        }

        Log.d(TAG, "Building notification: Title='$title', Message='$message'")

        val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        try {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
            Log.d(TAG, "Notification sent")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send notification: ${e.message}", e)
        }
    }
}
