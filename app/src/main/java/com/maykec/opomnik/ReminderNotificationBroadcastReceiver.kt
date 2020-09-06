package com.maykec.opomnik

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmTitle: String
        val extras  = intent?.extras
        if (extras != null) {
            alarmTitle= extras.getString("meds_name") as String;
        } else {
            alarmTitle = "can not get extras"
        }
        Log.d("ReminderNotification", "Alarm fired: " + alarmTitle)
        val channelId = "${context?.packageName}-${context?.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context!!, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_background)
            setContentTitle(alarmTitle)
            setContentText(alarmTitle)
            setStyle(NotificationCompat.BigTextStyle().bigText(alarmTitle)) // 6
            priority = NotificationCompat.PRIORITY_HIGH // 7
            setAutoCancel(true)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(25451, notificationBuilder.build())
    }
}