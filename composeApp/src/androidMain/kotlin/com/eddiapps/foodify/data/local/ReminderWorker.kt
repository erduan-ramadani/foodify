package com.eddiapps.foodify.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eddiapps.foodify.R
import com.eddiapps.foodify.domain.PreferencesInterface
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
                        getRandomDailyText()
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

    private fun getRandomDailyText(): String {
        val texts = applicationContext.resources.getStringArray(R.array.reminder_daily_texts)
        return texts.random()
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
        val channel = NotificationChannel(
            channelId,
            "Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)

        val intent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val largeIcon = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.mipmap.ic_launcher
        )


        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setColor(Color(0xFF4A9F6F).toArgb())
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(notificationId, notification)
    }
}