package com.example.my_bop_it_app

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import java.util.Random

class Game : AppCompatActivity(), SensorEventListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var paramsBGMusic :PlaybackParams
    private lateinit var mediaPlayerWin: MediaPlayer
    private lateinit var mediaPlayerLose: MediaPlayer

    private var currentAction:Actions = Actions.Tap

    private val handler = Handler(Looper.getMainLooper())
    private val handlerActionsTimer = Handler(Looper.getMainLooper())
    private var minSpeed = 0.8f
    private var currentSpeed = 0.8f
    private val speedIncrement = 0.1f
    private val maxSpeed = 1.3f
    private lateinit var textAction:TextView
    private lateinit var textTimerAction:TextView

    private lateinit var BestScoreText:TextView
    private lateinit var myScoreText:TextView

    private var currentTimeActions =0.0f
    private val maxTimeActions =5.0f
    private val incrementToTimerActions =0.1f
    private val TimeToIncrmentTimerActions = 100L

    private lateinit var gestureDetector: GestureDetector
    private val speedIncrementDelay = 1000L

    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private val SHAKE_THRESHOLD = 800

    private var myScore = 0;
    private var lost = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        lost = false
        //Música y sonidos
        currentTimeActions = maxTimeActions
        mediaPlayer = MediaPlayer.create(this, R.raw.bg)
        currentSpeed = minSpeed
        paramsBGMusic = PlaybackParams()
        paramsBGMusic.speed = minSpeed
        mediaPlayer.playbackParams = paramsBGMusic
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Programa el incremento gradual de la velocidad
        handler.postDelayed(IncrementMusicSpeed, speedIncrementDelay)
        handlerActionsTimer.postDelayed(IncrementTimerActions, TimeToIncrmentTimerActions)

        //Gestos
        gestureDetector = GestureDetector(this, MyGestureListener())

        //Sensores
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        //botones
        val backbtn = findViewById<Button>(R.id.backGameAC_btn)
        val winbtn = findViewById<Button>(R.id.winGameAC_btn)
        val losebtn = findViewById<Button>(R.id.loseGameAC_btn)
        val resetbtn = findViewById<Button>(R.id.reset_btn)

        backbtn.setOnClickListener(){
            val nextPage = Intent(this, HomeActivity::class.java)
            finish()
            startActivity(nextPage)
        }
        mediaPlayerWin= MediaPlayer.create(this, R.raw.win)
        winbtn.setOnClickListener(){
            mediaPlayerWin.start()
        }
        mediaPlayerLose= MediaPlayer.create(this, R.raw.lose)
        losebtn.setOnClickListener(){
            mediaPlayerLose.start()
        }
        resetbtn.setOnClickListener(){
            Reset()
        }


        textTimerAction = findViewById<TextView>(R.id.TimerActions)
        textAction = findViewById<TextView>(R.id.ActionToDo)
        BestScoreText = findViewById<TextView>(R.id.BestScoreValue)
        myScoreText = findViewById<TextView>(R.id.ScoreValue)

        currentAction = GetRandomAction()
        UpdateTextAction()
    }
    private val IncrementMusicSpeed = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying &&currentSpeed + speedIncrement <= maxSpeed) { // Límite de velocidad
                currentSpeed += speedIncrement
                //val playbackParams = PlaybackParams()
                //playbackParams.speed = currentSpeed
                //mediaPlayer.playbackParams = playbackParams
                paramsBGMusic.speed = currentSpeed
                mediaPlayer.playbackParams = paramsBGMusic
                handler.postDelayed(this, speedIncrementDelay)
            }
        }
    }
    private val IncrementTimerActions = object : Runnable {
        override fun run() {
            if (currentTimeActions - incrementToTimerActions >= 0) { // Límite de velocidad
                currentTimeActions -= incrementToTimerActions
                textTimerAction.text = String.format("%.1f",currentTimeActions)
                handlerActionsTimer.postDelayed(this, TimeToIncrmentTimerActions)
            }else{

                Lose()
                //Perder por tiempo
                //textAction.text = this.getString(R.string.Swipe_title)
            }
        }
    }
    private fun GetRandomAction():Actions {
        val random = Random()
        val values = Actions.values()
        return values[random.nextInt(values.size)]
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.pause()
        mediaPlayerWin.pause()
        mediaPlayerLose.pause()

        handler.removeCallbacksAndMessages(null)
    }
    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
        mediaPlayerWin.pause()
        mediaPlayerLose.pause()
        sensorManager?.unregisterListener(this)
    }
    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
    fun UpdateTextAction(){
        if(currentAction == Actions.Shake){
            textAction.text = this.getString(R.string.Shake_title)
        }else if(currentAction == Actions.Tap){
            textAction.text = this.getString(R.string.Tap_title)
        }else if(currentAction == Actions.LongTap){
            textAction.text = this.getString(R.string.LongTap_title)
        }
        myScoreText.text = myScore.toString()
    }
    fun Lose(){
        mediaPlayer.stop()
        mediaPlayerLose.start()
        lost = true
    }
    fun Win(){
        mediaPlayer.pause()
        mediaPlayerWin.start()
    }
    fun Reset(){
        currentSpeed = minSpeed
        //val playbackParams = PlaybackParams()
        //playbackParams.speed = minSpeed
        //mediaPlayer.playbackParams = playbackParams
        //mediaPlayer.start()
        currentTimeActions = maxTimeActions
        currentAction = GetRandomAction()
        paramsBGMusic.speed = currentSpeed
        mediaPlayer.playbackParams = paramsBGMusic

        UpdateTextAction()
        if(lost){
            handler.postDelayed(IncrementMusicSpeed, speedIncrementDelay)
            handlerActionsTimer.postDelayed(IncrementTimerActions, TimeToIncrmentTimerActions)
            lost = false;
        }

    }
    fun CorrectAction(){
        currentTimeActions = maxTimeActions
        currentAction = GetRandomAction()
        UpdateTextAction()
        myScore++
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
                //if(currentAction != Actions.Fling){
                //    Lose()
                //}else{
                //    CorrectAction()
                    //Toast.makeText(this@Game, "Evento onFling", Toast.LENGTH_SHORT).show()
                //}
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            if(currentAction != Actions.LongTap){
                Lose()
            }else{
                CorrectAction()
            }
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return super.onSingleTapUp(e)
            if(currentAction != Actions.Tap){
                Lose()
            }else{
                CorrectAction()
            }
        }
    }
    enum class Actions {
        Shake,
        Tap,
        LongTap
    }
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()

            if ((currentTime - lastUpdate) > 100) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    if(currentAction != Actions.Shake){
                        Lose()
                    }else{
                        CorrectAction()
                        //Toast.makeText(this, "Agitar", Toast.LENGTH_SHORT).show()
                    }
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }
    companion object {
        private var lastX: Float = 0f
        private var lastY: Float = 0f
        private var lastZ: Float = 0f
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}