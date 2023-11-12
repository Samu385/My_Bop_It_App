package com.example.my_bop_it_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager


class SplashScreenActivity : AppCompatActivity() {

    //private val splashTimeOut: Long = 3000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //val sharedPreferences: SharedPreferences = getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE)

        val value = sharedPreferences.getString("time_splash","1000")
        val seconds = value!!.toLong()
        //Toast.makeText(this, value,Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, seconds)
    }
}