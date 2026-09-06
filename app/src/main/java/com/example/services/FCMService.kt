package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_DEBUG", "Novo Token gerado: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Nova Mensagem"
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: "Você recebeu uma nova notificação."

        Log.d("FCM_DEBUG", "Mensagem Recebida! Título: $title | Corpo: $body | Data: ${remoteMessage.data}")
        sendNotification(title, body)
    }

    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "bairroo_delivery_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Só cria o canal se o Android for 8.0 (Oreo) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Atualizações de Pedidos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações sobre o status das suas entregas e corridas"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        try {
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            notificationManagerCompat.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
            Log.d("FCM_DEBUG", "Notificação exibida no sistema com sucesso: $title")
        } catch (e: SecurityException) {
            Log.e("FCM_DEBUG", "Permissão POST_NOTIFICATIONS não concedida pelo usuário", e)
        } catch (e: Exception) {
            Log.e("FCM_DEBUG", "Erro ao exibir notificação", e)
        }
    }
}
