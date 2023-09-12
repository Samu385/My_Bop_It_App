package com.example.my_bop_it_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val btn = findViewById<Button>(R.id.Back_btn)

        btn.setOnClickListener(View.OnClickListener(){

            val nextPage = Intent(this, HomeActivity::class.java);
            startActivity(nextPage);
        })
    }
}