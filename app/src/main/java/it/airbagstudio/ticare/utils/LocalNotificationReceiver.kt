package it.airbagstudio.ticare.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.internal.ContextUtils.getActivity
import it.airbagstudio.ticare.MainActivity
import it.airbagstudio.ticare.R

class LocalNotificationReceiver: BroadcastReceiver() {
    private var notificationManager: NotificationManagerCompat? = null
    private val notificationId = 123
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null) {
            createNotificationChannel(p0)
        }
        val tapResultIntent = Intent(p0, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = getActivity( p0,0,tapResultIntent,FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

        val notification = p0?.let {
            NotificationCompat.Builder(it, "sync")
                .setContentTitle("Ti-care")
                .setContentText(p0.getText(R.string.not_sync_data_present))
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()
        }
        notificationManager = p0?.let { NotificationManagerCompat.from(it) }
        notification?.let { if (ActivityCompat.checkSelfPermission(
                p0,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
            notificationManager?.notify(notificationId, it) }
    }

    private fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("sync", "Sync alarm Notification Channel", importance).apply {
            description = "Notification for sync"
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}