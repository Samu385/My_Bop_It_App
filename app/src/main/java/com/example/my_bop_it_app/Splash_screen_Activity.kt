package com.example.my_bop_it_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity


class Splash_screen_Activity : AppCompatActivity() {

    private val splashTimeOut: Long = 3000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val value = sharedPreferences.getString("time_spash","1000")
        var seconds = value!!.toLong()
        seconds *= 1000;
        // Configurar un temporizador para mostrar el SplashScreen durante splashTimeOut milisegundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Crear un Intent para iniciar la siguiente actividad (por ejemplo, MainActivity)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual para que el usuario no pueda regresar al SplashScreen
        }, seconds)
    }
}