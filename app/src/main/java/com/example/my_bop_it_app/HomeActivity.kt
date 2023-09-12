package com.example.my_bop_it_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View;
import android.widget.Button;
import android.content.Intent;

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.Acerca_De_btn)

        btn.setOnClickListener(View.OnClickListener(){

            val nextPage = Intent(this, AboutActivity::class.java);
            startActivity(nextPage);
        })
        val btnSettings = findViewById<Button>(R.id.Preferencias_btn)
        btnSettings.setOnClickListener(View.OnClickListener(){

            val nextPage = Intent(this, SettingsActivity::class.java);
            startActivity(nextPage);
        })

        val btnPlay = findViewById<Button>(R.id.Jugar_btn)
        btnPlay.setOnClickListener(View.OnClickListener(){

            val nextPage = Intent(this, Game::class.java);
            startActivity(nextPage);
        })


    }
}