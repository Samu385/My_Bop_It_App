package com.example.my_bop_it_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.Intent


class Splash_screen_Activity : AppCompatActivity() {

    private val splashTimeOut: Long = 3000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Configurar un temporizador para mostrar el SplashScreen durante splashTimeOut milisegundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Crear un Intent para iniciar la siguiente actividad (por ejemplo, MainActivity)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual para que el usuario no pueda regresar al SplashScreen
        }, splashTimeOut)
    }
}