package com.eddiapps.foodify.data.remote.firebase

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.eddiapps.foodify.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token an dein Backend schicken
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        showNotification(title, body)
    }

    fun showNotification(title: String?, body: String?) {
        val builder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }
}