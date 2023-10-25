package com.example.my_bop_it_app

import android.content.Intent
import android.media.MediaPlayer
import android.media.PlaybackParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast

class Game : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerWin: MediaPlayer
    private lateinit var mediaPlayerLose: MediaPlayer

    private val handler = Handler(Looper.getMainLooper())
    private var currentSpeed = 0.8f
    private val speedIncrement = 0.1f
    private val maxSpeed = 1.8f
    private lateinit var gestureDetector: GestureDetector
    private val speedIncrementDelay = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        mediaPlayer = MediaPlayer.create(this, R.raw.bg)
        val playbackParams = PlaybackParams()
        playbackParams.speed = currentSpeed
        mediaPlayer.playbackParams = playbackParams
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Programa el incremento gradual de la velocidad
        handler.postDelayed(incrementSpeedRunnable, speedIncrementDelay)

        gestureDetector = GestureDetector(this, MyGestureListener())

        //botones
        val backbtn = findViewById<Button>(R.id.backGameAC_btn)
        val winbtn = findViewById<Button>(R.id.winGameAC_btn)
        val losebtn = findViewById<Button>(R.id.loseGameAC_btn)


        backbtn.setOnClickListener(){
            val nextPage = Intent(this, HomeActivity::class.java)
            finish()
            startActivity(nextPage)
        }
        winbtn.setOnClickListener(){
            mediaPlayerWin= MediaPlayer.create(this, R.raw.win)
            mediaPlayerWin.start()
        }
        losebtn.setOnClickListener(){
            mediaPlayerLose= MediaPlayer.create(this, R.raw.lose)
            mediaPlayerLose.start()
        }


    }
    //Incrementa la velocidad de la música de fondo
    private val incrementSpeedRunnable = object : Runnable {
        override fun run() {
            if (currentSpeed + speedIncrement <= maxSpeed) { // Límite de velocidad
                currentSpeed += speedIncrement
                val playbackParams = PlaybackParams()
                playbackParams.speed = currentSpeed
                mediaPlayer.playbackParams = playbackParams
                handler.postDelayed(this, speedIncrementDelay)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mediaPlayerWin.release()
        mediaPlayerLose.release()
        handler.removeCallbacksAndMessages(null)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if(velocityY> velocityX){
                Toast.makeText(this@Game, "Evento onFling", Toast.LENGTH_SHORT).show()
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
}