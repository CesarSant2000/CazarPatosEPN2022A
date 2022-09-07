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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import com.santacruz.cesar.cazarpatossc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textViewUser: TextView
    private lateinit var textViewCounter: TextView
    lateinit var textViewTime: TextView
    private lateinit var imageViewDuck: ImageView
    lateinit var user:String
    private lateinit var database: DatabaseReference
    private var counter = 0
    private var screenWidth = 0
    private var screenHeight = 0
    var gameOver = false
    private var mediaPlayer: MediaPlayer? = null

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
        database = Firebase.database.reference
        mediaPlayer = MediaPlayer.create(this, R.raw.gunshot)
        MobileAds.initialize(this) {}

        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        //Get user from login screen
        val extras = intent.extras ?: return
        user = (extras.getString(EXTRA_LOGIN)?.substringBefore("@")) ?: "Unknown"
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
            if (! mediaPlayer!!.isPlaying){
                mediaPlayer?.start()
            }
            textViewCounter.text = counter.toString()
            imageViewDuck.setImageResource(R.drawable.duck_clicked)

            //Delay event that plays after 500 milliseconds
            lifecycleScope.launch {
                delay(500)
                imageViewDuck.setImageResource(R.drawable.duck)
                duckMoves()
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //exit game
            R.id.action_exit -> {
                timeCounter.cancel()
                finish()
                true
            }
            //reset game
            R.id.action_new_game -> {
                restartGame()
                true
            }
            //go to link
            R.id.action_online_game -> {
                onlineGame()
                true
            }
            R.id.action_ranking -> {
                val intent = Intent(this, RankingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                timeCounter.cancel()
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
            val playerName = textViewUser.text.toString()
            val huntedDucks = textViewCounter.text.toString()
            processHuntedDucksScore(playerName, huntedDucks.toInt())
            processHuntedDucksScoreRTDB(playerName, huntedDucks.toInt())
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

    private fun onlineGame() {
        timeCounter.cancel()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://duckhuntjs.com/"))
        startActivity(intent)
    }

    fun processHuntedDucksScore(playerName:String, huntedDucks:Int){
        val player = Player(playerName,huntedDucks)
        //Try to get ranking specific id,
        //if exists update otherwise create it.
        val db = Firebase.firestore
        db.collection("ranking")
            .whereEqualTo("user", player.user)
            .get()
            .addOnSuccessListener { documents ->
                if(documents!= null && documents.documents.isNotEmpty()
                ){
                    val idDocument = documents.documents[0].id
                    updatePlayersScore(idDocument, player)
                }
                else{
                    enterPlayersScore(player)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error getting documents", exception)
                Toast.makeText(this, "Error getting players data", Toast.LENGTH_LONG).show()
            }
    }

    fun processHuntedDucksScoreRTDB(playerName: String, huntedDucks: Int){
        val playerId = playerName.replace('.', '_')
        val player = Player(playerName,huntedDucks)
        database.child("ranking").child(playerId).setValue(player)
    }

    private fun enterPlayersScore(player:Player){
        val db = Firebase.firestore
        db.collection("ranking")
            .add(player)
            .addOnSuccessListener {
                Toast.makeText(this,"User score enter successfully", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error adding document", exception)
                Toast.makeText(this,"Error writing user score", Toast.LENGTH_LONG).show()
            }
    }
    private fun updatePlayersScore(idDocument:String, player:Player){
        val db = Firebase.firestore
        db.collection("ranking")
            .document(idDocument)
            //.update(contactHashMap)
            .set(player) //another way to update
            .addOnSuccessListener {
                Toast.makeText(this,"User score updated successfully", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error updating document", exception)
                Toast.makeText(this,"Error updating score" , Toast.LENGTH_LONG).show()
            }
    }

    override fun onStop() {
        Log.w(EXTRA_LOGIN, "Play canceled")
        timeCounter.cancel()
        textViewTime.text = getString(R.string.initial_time)
        gameOver = true
        mediaPlayer?.stop()
        super.onStop()
    }
    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onPause() {
        timeCounter.cancel()
        super.onPause()
    }
}

