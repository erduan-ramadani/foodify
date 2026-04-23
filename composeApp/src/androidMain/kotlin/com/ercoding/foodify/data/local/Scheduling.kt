package com.ercoding.foodify.data.local

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class Scheduling(
    private val context: Context
) {
    fun schedule() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(getInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "daily_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )

        Log.d("ReminderWorker", "Worker läuft")
    }

    fun oneTimeNotification() {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .build()

        WorkManager.getInstance(context)
            .enqueue(request)
    }

    fun getInitialDelay(): Long {
        val now = Calendar.getInstance()

        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 13)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (now.after(target)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}