package com.santacruz.cesar.cazarpatossc

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import java.util.*
import com.santacruz.cesar.cazarpatossc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textViewUser: TextView
    private lateinit var textViewCounter: TextView
    lateinit var textViewTime: TextView
    private lateinit var imageViewDuck: ImageView
    private var counter = 0
    private var screenWidth = 0
    private var screenHeight = 0
    var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Variables initializations
        textViewUser = findViewById(R.id.textViewUser)
        textViewCounter = findViewById(R.id.textViewCounter)
        textViewTime = findViewById(R.id.textViewTime)
        imageViewDuck = findViewById(R.id.imageViewDuck)

        //Get user from login screen
        val extras = intent.extras ?: return
        val user = (extras.getString(EXTRA_LOGIN)?.substringBefore("@")) ?: "Unknown"
        textViewUser.text = user

        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Gets the screen width and height
        screenInitializer()

        //Game countdown timer
        countdownInitializer()

        //Click event over the duck image
        imageViewDuck.setOnClickListener {
            if (gameOver) return@setOnClickListener
            counter++
            MediaPlayer.create(this, R.raw.gunshot).start()
            textViewCounter.text = counter.toString()
            imageViewDuck.setImageResource(R.drawable.duck_clicked)

            //Delay event that plays after 500 milliseconds
            lifecycleScope.launch {
                delay(500)
                imageViewDuck.setImageResource(R.drawable.duck)
                duckMoves()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //exit game
        if (item.itemId == R.id.action_exit) {
            finish()
        }
        //reset game
        if (item.itemId == R.id.action_new_game) {
            restartGame()
        }
        //go to link
        if (item.itemId == R.id.action_online_game) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://duckhuntjs.com/"))
            startActivity(intent)
        }
        return when (item.itemId) {
            R.id.action_exit -> true
            R.id.action_new_game -> true
            R.id.action_online_game -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun screenInitializer() {
        // 1. Get phone screen size
        val display = this.resources.displayMetrics
        screenWidth = display.widthPixels
        screenHeight = display.heightPixels
    }

    private fun duckMoves() {
        val min = imageViewDuck.width /2
        val maximoX = screenWidth - imageViewDuck.width
        val maximoY = screenHeight - imageViewDuck.height
        // We generate 2 random numbers, for x and y axis
        val randomX = Random().nextInt(maximoX - min ) + 1
        val randomY = Random().nextInt(maximoY - min ) + 1

        // We use the 2 random numbers to move the duck to a new position
        imageViewDuck.x = randomX.toFloat()
        imageViewDuck.y = randomY.toFloat()
    }
    private var timeCounter = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val remainingSeconds = millisUntilFinished / 1000
            val stringRemainingSeconds = "${remainingSeconds}s"
            textViewTime.text = stringRemainingSeconds
        }
        override fun onFinish() {
            textViewTime.text = getString(R.string.initial_time)
            gameOver = true
            showGameOverDialog()
        }
    }
    private fun countdownInitializer() {
        timeCounter.start()
    }
    private fun showGameOverDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setIcon(R.drawable.duck_hunt_logo)
            .setMessage("Congratulations!!\nYou have hunt $counter ducks")
            .setTitle("Game Over")
            .setPositiveButton("Restart"
            ) { _, _ ->
                restartGame()
            }
            .setNegativeButton("Exit"
            ) { _, _ ->
                //dialog.dismiss()
            }
        builder.create().show()
    }
    private fun restartGame(){
        counter = 0
        gameOver = false
        timeCounter.cancel()
        textViewCounter.text = counter.toString()
        duckMoves()
        countdownInitializer()
    }

}

