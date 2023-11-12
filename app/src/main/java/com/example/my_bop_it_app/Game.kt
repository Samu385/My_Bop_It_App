package com.example.my_bop_it_app

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.util.Random

class Game : AppCompatActivity(), SensorEventListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var paramsBGMusic :PlaybackParams
    private lateinit var mediaPlayerWin: MediaPlayer
    private lateinit var mediaPlayerLose: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferencesEditor: Editor
    private var currentAction:Actions = Actions.Tap

    private val handler = Handler(Looper.getMainLooper())
    private val handlerActionsTimer = Handler(Looper.getMainLooper())
    private val handlerDetectLose = Handler(Looper.getMainLooper())
    private var minSpeed = 0.8f
    private var currentSpeed = 0.8f
    private val speedIncrement = 0.1f
    private val maxSpeed = 1.4f
    private lateinit var textAction:TextView
    private lateinit var textTimerAction:TextView

    private lateinit var BestScoreText:TextView
    private lateinit var myScoreText:TextView

    private var currentTimeActions =0.0f
    private var maxTimeActions = 7.0f
    private var decrementTimeActions = 0.2f
    private var totalMaxTimeActions = 15.0f
    private var minTimeActions = 3.0f
    private val incrementToTimerActions =0.1f
    private val TimeToIncrmentTimerActions = 100L

    private lateinit var gestureDetector: GestureDetector
    private val speedIncrementDelay = 5000L

    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private val SHAKE_THRESHOLD = 800

    private var myScore = 0;
    private var maxScore = 0;
    private var lost = false
    private var canDetectLose = true
    private val timeToDetectLose = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferencesEditor = sharedPreferences.edit()
        updateDifficulty()
        lost = false

        //Música y sonidos
        maxTimeActions = totalMaxTimeActions
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

        backbtn.setOnClickListener{
            val nextPage = Intent(this, HomeActivity::class.java)
            finish()
            startActivity(nextPage)
        }
        mediaPlayerWin= MediaPlayer.create(this, R.raw.win)
        winbtn.setOnClickListener{
            mediaPlayerWin.start()
        }
        mediaPlayerLose= MediaPlayer.create(this, R.raw.lose)
        losebtn.setOnClickListener{
            mediaPlayerLose.start()
        }
        resetbtn.setOnClickListener{
            reset()
        }


        textTimerAction = findViewById(R.id.TimerActions)
        textAction = findViewById(R.id.ActionToDo)
        BestScoreText = findViewById(R.id.BestScoreValue)
        myScoreText = findViewById(R.id.ScoreValue)

        currentAction = getRandomAction()

        updateTextAction()
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

                endGame()
            }
        }
    }
    private val DetectLose = object : Runnable {
        override fun run() {
            canDetectLose = true
        }
    }
    private fun updateDifficulty(){

        val difficultyString = sharedPreferences.getString("difficulty", "1.0")
        val difficulty = difficultyString?.toFloat() ?: 1.0f
        val maxScoreStr = "max_score$difficultyString"
        //Toast.makeText(this, maxScoreStr,Toast.LENGTH_SHORT).show()
        maxScore = sharedPreferences.getInt(maxScoreStr,0)

        totalMaxTimeActions /= difficulty
        decrementTimeActions *= difficulty
        minTimeActions /= difficulty
    }
    private fun getRandomAction():Actions {
        val random = Random()
        val values = Actions.values()
        var newAction = values[random.nextInt(values.size)]
        while(newAction == currentAction){
            newAction = values[random.nextInt(values.size)]
        }
        return newAction
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
    private fun updateTextAction(){
        when (currentAction) {
            Actions.Cut -> {
                textAction.text = this.getString(R.string.Fling_title)
            }
            Actions.Shake -> {
                textAction.text = this.getString(R.string.Shake_title)
            }
            Actions.Tap -> {
                textAction.text = this.getString(R.string.Tap_title)
            }
            Actions.LongTap -> {
                textAction.text = this.getString(R.string.LongTap_title)
            }
        }
        myScoreText.text = myScore.toString()
        textTimerAction.text = String.format("%.1f",currentTimeActions)
        BestScoreText.text = maxScore.toString()
    }
    private fun endGame(){
        if(updateMaxScore()){
            win()
        }else{
            lose()
        }

    }
    private fun lose(){
        if(canDetectLose){
            maxTimeActions = totalMaxTimeActions
            mediaPlayer.pause()
            mediaPlayerLose.start()
            lost = true
            currentTimeActions  = 0.0f
        }
    }
    private fun win(){
        maxTimeActions = totalMaxTimeActions
        mediaPlayer.pause()
        mediaPlayerWin.start()
        lost = true
        currentTimeActions  = 0.0f
    }
    private fun reset(){
        maxTimeActions = totalMaxTimeActions
        currentSpeed = minSpeed
        //val playbackParams = PlaybackParams()
        paramsBGMusic.speed = minSpeed
        mediaPlayer.playbackParams = paramsBGMusic
        currentTimeActions = maxTimeActions
        currentAction = getRandomAction()
        myScore = 0
        handler.removeCallbacks(IncrementMusicSpeed)
        handler.postDelayed(IncrementMusicSpeed, speedIncrementDelay)
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
        updateTextAction()
        if(lost){

            handlerActionsTimer.postDelayed(IncrementTimerActions, TimeToIncrmentTimerActions)
            lost = false;
        }
    }
    private fun correctAction(){
        if(maxTimeActions - decrementTimeActions >= minTimeActions){
            maxTimeActions -= decrementTimeActions
        }
        currentTimeActions = maxTimeActions
        currentAction = getRandomAction()
        myScore++
        canDetectLose = false
        handlerDetectLose.postDelayed(DetectLose, timeToDetectLose)


        updateTextAction()
    }
    private fun updateMaxScore():Boolean{
        if(myScore > maxScore){
            val difficultyString = sharedPreferences.getString("difficulty", "1.0")
            preferencesEditor.putInt("max_score$difficultyString", myScore)
            preferencesEditor.apply()
            maxScore = myScore
            Toast.makeText(this, "Nuevo puntaje máixmo", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
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
            if(currentAction != Actions.Cut){
                endGame()
            }else{
                correctAction()
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            if(currentAction != Actions.LongTap){
                endGame()
            }else{
                correctAction()
            }

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {

            if(currentAction != Actions.Tap){
                lose()
            }else{
                correctAction()
            }

            return super.onSingleTapUp(e)
        }

    }
    enum class Actions {
        Shake,
        Tap,
        LongTap,
        Cut
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
                        endGame()
                    }else{
                        correctAction()
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