package com.example.finalapp

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Define default values for title and message
        var title = "Default Title"
        var message = "Default Message"

        // Check if message contains a data payload and handle it.
        if (remoteMessage.data.isNotEmpty()) {
            title = remoteMessage.data["title"] ?: title
            message = remoteMessage.data["message"] ?: message
        }

        // Check if message contains a notification payload and override title and message.
        remoteMessage.notification?.let {
            title = it.title ?: title
            message = it.body ?: message
        }

        // Display notification regardless of app state
        displayNotification(title, message)
    }

    private fun displayNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(this, "444567") // Use your actual channel ID
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.burger) // Replace with your notification icon
        // ... other notification configurations

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
