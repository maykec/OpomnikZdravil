package com.maykec.opomnik.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import com.maykec.opomnik.Constants
import com.maykec.opomnik.R
import com.maykec.opomnik.model.Notification
import java.util.*

class ReminderNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "RNReceiver"
        const val NOTIFICATION_ID = 25451
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationTitle: String
        val extras  = intent?.extras
        notificationTitle = if (extras != null) {
            extras.getString(Constants.MEDS_NAME_INTENT_EXTRA) as String;
        } else {
            " " // fallback to some message
        }
        Log.d(TAG, "Alarm fired: $notificationTitle")

        // notify database that event reminder is about to fire
        sendNotificationToFirebase(context, notificationTitle)
    }

    private fun sendNotificationToFirebase(context: Context?, medsName: String) {
        var database = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DATA_BASE_NOTIFICATIONS_NAME)
        val notificationId = database.push().key as String
        var notification = Notification(id = notificationId,
            medsName = medsName, timestampCreated = System.currentTimeMillis(), timestampConfirmed = 0)
        database.child(notificationId).setValue(notification).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Notification event stored")
                createNotification(context, notificationId, medsName)
            } else {
                // if notification is not stored on firebase we still want to show it to user
                Log.d(TAG, "Notification event not stored")
                createNotification(context, "-1", medsName) // set dummy value -1 for notificationId
            }

        }
    }

    private fun createNotification(context: Context?, notificationId: String, notificationText: String) {
        val intent = Intent(context, NotificationConfirmationReceiver::class.java)
        intent.putExtra(Constants.NOTIFICATION_ID_INTENT_EXTRA ,NOTIFICATION_ID)
                .putExtra(Constants.FIREBASE_EVENT_ID_INTENT_EXTRA, notificationId)// notification ID to update on firebase

        val notificationConfirmPendingIntent = PendingIntent.getBroadcast(context, Random().nextInt(), intent,0)

        val channelId = "${context?.packageName}-${context?.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context!!, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_background)
            setContentTitle(notificationText)
            addAction(0, context.getString(R.string.confirm_notification), notificationConfirmPendingIntent)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(false)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}