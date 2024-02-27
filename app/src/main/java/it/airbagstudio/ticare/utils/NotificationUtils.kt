package it.airbagstudio.ticare.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

fun removePendingNotifications(context: Context) {
    val alarmManager =
        ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager
    val alarmIntent = Intent(context, LocalNotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

fun scheduleNotification(context: Context){
    val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager
    val alarmIntent = Intent(context, LocalNotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent,
        PendingIntent.FLAG_IMMUTABLE)
    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(22), pendingIntent)
}