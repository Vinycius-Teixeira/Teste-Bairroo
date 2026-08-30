package com.example.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    private val notificationService = NotificationService(this)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCMService", "From: ${remoteMessage.from}")
        
        remoteMessage.notification?.let {
            Log.d("FCMService", "Message Notification Body: ${it.body}")
            notificationService.showNotification(it.title ?: "Nova Mensagem", it.body ?: "Você recebeu uma nova notificação.")
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCMService", "Refreshed token: $token")
    }
}
