package com.example.bitirme_projesi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Bir bildirim geldiğinde bu fonksiyon otomatik tetiklenir
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "TripBuddy"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Yeni bir bildirim var!"

        showNotification(title, body)
    }

    // Bildirimi telefonun üstünden düşüren asıl kod
    private fun showNotification(title: String, body: String) {
        val channelId = "TripBuddy_Channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 ve üzeri için "Bildirim Kanalı" zorunludur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "TripBuddy Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Şimdilik Android'in varsayılan ikonu
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Anında üstte belirmesi için YÜKSEK öncelik
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}