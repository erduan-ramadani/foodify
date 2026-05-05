package com.eddiapps.foodify.data.local

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

class Scheduling(
    private val context: Context
) {
    companion object {
        private const val DAILY_REMINDER_WORK_NAME = "daily_reminder"
        private const val WEEKLY_REMINDER_WORK_NAME = "weekly_reminder"
    }

    fun schedule() {
        val dailyRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(getInitialDailyDelay(), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("type" to "daily"))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                DAILY_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyRequest
            )

        val weeklyRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            7, TimeUnit.DAYS
        )
            .setInitialDelay(getInitialWeeklyDelay(), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("type" to "weekly"))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WEEKLY_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                weeklyRequest
            )

        Log.d("ReminderWorker", "Dailyworker läuft")
        Log.d("ReminderWorker", "Weeklyworker läuft")
    }

    private fun getInitialDailyDelay(): Long {
        val now = Calendar.getInstance()

        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (now.after(target)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }

    private fun getInitialWeeklyDelay(): Long {
        val now = Calendar.getInstance()

        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (now.after(target)) {
            target.add(Calendar.WEEK_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(WEEKLY_REMINDER_WORK_NAME)
        Log.d("ReminderWorker", "DailyWorker gestoppt")
        Log.d("ReminderWorker", "WeeklyWorker gestoppt")
    }
}