package com.maykec.opomnik

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReminderService : Service() {

    companion object {
        const val TAG = "ReminderService"
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Notification Service started by user.", Toast.LENGTH_LONG).show();
        setRemindersListener()
        return START_STICKY;
    }

    fun setRemindersListener() {
        var database = FirebaseDatabase.getInstance().getReference("opomnikzdravil")
        database.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "onCancelled")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        Log.d(TAG, data.value.toString())
                    }
                }
            }

        })

    }
}