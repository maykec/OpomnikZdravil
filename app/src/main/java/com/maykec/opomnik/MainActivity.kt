package com.maykec.opomnik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btStartService: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btStartService = findViewById(R.id.bt_start_service)
        btStartService.setOnClickListener(this)
    }

    private fun startService() {
        val intent = Intent(this, ReminderService::class.java)
        startService(intent)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.bt_start_service -> startService()
        }
    }
}