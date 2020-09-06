package com.maykec.opomnik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var btStartService: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btStartService = findViewById(R.id.bt_start_service)
        btStartService.setOnClickListener {
            startService()
        }
    }

    fun startService() {
        val intent = Intent(this, ReminderService::class.java)
        startService(intent)
    }
}