package com.maykec.opomnik.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.number.IntegerWidth
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.FirebaseDatabase
import com.maykec.opomnik.Constants

class NotificationConfirmationReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "NConfirmationReceiver"
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        var notificationId = 0
        var firebaseEntryId = ""

        var extras = intent?.extras
        if (extras != null) {
            notificationId = extras.getInt(Constants.NOTIFICATION_ID_INTENT_EXTRA) as Int
            firebaseEntryId = extras.getString(Constants.FIREBASE_EVENT_ID_INTENT_EXTRA).toString()
        }

        // update firebase event when user confirms notification
        var database = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DATA_BASE_NOTIFICATIONS_NAME)
        database.child(firebaseEntryId).child("timestampConfirmed").setValue(System.currentTimeMillis())

        // cancel notification
        var notificationManager = context?.getSystemService(NotificationManager::class.java)
        notificationManager?.cancel(notificationId)



        Log.d(TAG, "notificationId: $notificationId firebaseEntryId: $firebaseEntryId")
    }
}