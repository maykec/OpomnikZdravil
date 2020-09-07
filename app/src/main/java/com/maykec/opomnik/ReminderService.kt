package com.maykec.opomnik

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.maykec.opomnik.model.Reminder
import com.maykec.opomnik.notification.NotificationHelper
import com.maykec.opomnik.notification.ReminderNotificationReceiver
import java.util.*

class ReminderService : Service() {

    companion object {
        const val TAG = "ReminderService"
    }

    lateinit var sharedPreferences: SharedPreferences
    lateinit var alarmManager: AlarmManager

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service start by user")
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel.")

        // get local storage
        sharedPreferences = getSharedPreferences(baseContext.packageName, Context.MODE_PRIVATE)
        // to manage alarms
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setRemindersListener()

        //we want to restart service if it shuts down
        return START_STICKY;
    }

    private fun setRemindersListener() {
        var database = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DATA_BASE_EVENTS_NAME)
        database.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "onCancelled")
            }

            @SuppressLint("NewApi")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        createAlarm(data)
                    }
                }
            }
        })
    }

    fun createAlarm(data: DataSnapshot) {
        // check if key alarm is already stored in databse. Better way to implement this would be to use DAO database
        // or something similar
        val reminder = data.getValue(Reminder::class.java)!!
        if (!sharedPreferences.contains(reminder.id)) {
            val timeString =  reminder.timeToTake // Could be done much prettier
            val hour = timeString.split(":")[0].toInt()
            val minute = timeString.split(":")[1].toInt()

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            val intent = Intent(baseContext, ReminderNotificationReceiver::class.java)
            intent.putExtra(Constants.MEDS_NAME_INTENT_EXTRA, reminder.medsName)
            val alarmIntent = PendingIntent.getBroadcast(baseContext, Random().nextInt(), intent,0)

            alarmManager?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )

            with (sharedPreferences.edit()) {
                putString(reminder.id, "")
                commit()
                Log.d(TAG, "Alarm ID stored: " + reminder.id)
            }
        } else {
            Log.d(TAG, "onDataChange() Alarm with ID already exists: " +reminder.id)
        }
    }
}