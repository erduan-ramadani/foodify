package com.ercoding.foodify.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ercoding.foodify.R
import com.ercoding.foodify.domain.PreferencesInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val prefRepository: PreferencesInterface by inject()

    override suspend fun doWork(): Result {
        val type = inputData.getString("type") ?: "daily"

        when (type) {
            "daily" -> {
                // Heute-Check + Daily Notification
                val today = LocalDate.now()
                val hasTrackedToday = prefRepository.getNutritionEntries().any {
                    Instant.ofEpochMilli(it.createdAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate() == today
                }
                if (!hasTrackedToday)
                    showNotification(
                        1,
                        applicationContext.getString(R.string.reminder_notification_title),
                        applicationContext.getString(R.string.reminder_daily_notification_text)
                    )
            }

            "weekly" -> {
                showNotification(
                    2,
                    applicationContext.getString(R.string.reminder_notification_title),
                    applicationContext.getString(
                        R.string.reminder_weekly_notification_text
                    )
                )
            }
        }
        return Result.success()
    }

    private fun showNotification(
        notificationId: Int,
        title: String,
        text: String
    ) {
        val channelId = "reminder_channel"
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Channel nur einmal erstellen (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        manager.notify(notificationId, notification)
    }
}