package com.maykec.opomnik

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ReminderService : Service() {

    companion object {
        const val TAG = "ReminderService"
        const val CHANNEL_ID = "4545151"
    }

    lateinit var sharedPrefernces: SharedPreferences
    lateinit var alarmManager: AlarmManager

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Notification Service started by user.", Toast.LENGTH_LONG).show();
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel.")

        // get local storage
        sharedPrefernces = getSharedPreferences(baseContext.packageName, Context.MODE_PRIVATE)
        // to manage alarms
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setRemindersListener()
        return START_STICKY;
    }

    fun setRemindersListener() {
        var database = FirebaseDatabase.getInstance().getReference("opomnikzdravil")
        database.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "onCancelled")
            }

            @SuppressLint("NewApi")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        // check if key exists
                        if (!sharedPrefernces.contains(data.child("id").value.toString())) {
                            // construct alarm
                            val timeString =  data.child("timeToTake").value.toString() // Could be done much prettier
                            val hour = timeString.split(":")[0].toInt()
                            val minute = timeString.split(":")[1].toInt()

                            val calendar: Calendar = Calendar.getInstance().apply {
                                timeInMillis = System.currentTimeMillis()
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                            val intent = Intent(baseContext, ReminderNotificationBroadcastReceiver::class.java)
                            intent.putExtra("meds_name", data.child("medsName").value.toString())
                            val alarmIntent = PendingIntent.getBroadcast(baseContext, 0, intent,0)

                            alarmManager?.setInexactRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                AlarmManager.INTERVAL_DAY,
                                alarmIntent
                            )

                            with (sharedPrefernces.edit()) {
                                putString(data.child("id").value.toString(), "")
                                commit()
                                Log.d(TAG, "Alarm ID stored")
                            }

                            Log.d(TAG, "Alarm for: " + hour + " : " + minute + " is set")

                        } else {
                            Log.d(TAG, "onDataChange() Alardm ID exiests: " + data.value.toString())
                        }
                    }
                }
            }

        })
    }
}