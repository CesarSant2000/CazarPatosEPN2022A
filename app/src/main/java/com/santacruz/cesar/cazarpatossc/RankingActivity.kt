package com.santacruz.cesar.cazarpatossc

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        /*
        val players = arrayListOf<Player>()
        players.add(Player("Player1",10))
        players.add(Player("Player2",6))
        players.add(Player("Player3",3))
        players.add(Player("Player4",9))
        players.sortByDescending { it.huntedDucks }

        val recyclerViewRanking: RecyclerView = findViewById(R.id.recyclerViewRanking)
        recyclerViewRanking.layoutManager = LinearLayoutManager(this)
        recyclerViewRanking.adapter = RankingAdapter(players)
        recyclerViewRanking.setHasFixedSize(true)
        */
        // checkPlayersScore()
        checkPlayersScoreRTDB()
    }

    private fun checkPlayersScore() {
        val db = Firebase.firestore
        db.collection("ranking")
            .orderBy("huntedDucks", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                Log.d(EXTRA_LOGIN, "Success getting documents")
                val players = ArrayList<Player>()
                for (document in result) {
                    val player = document.toObject(Player::class.java)
                    //val player = document.toObject<Player>()
                    players.add(player)
                }
                //Populate RecyclerView data with my adapter.
                val recyclerViewRanking: RecyclerView = findViewById(R.id.recyclerViewRanking)
                recyclerViewRanking.layoutManager = LinearLayoutManager(this)
                recyclerViewRanking.adapter = RankingAdapter(players)
                recyclerViewRanking.setHasFixedSize(true)
            }
            .addOnFailureListener { exception ->
                Log.w(EXTRA_LOGIN, "Error getting documents.", exception)
                Toast.makeText(this, "Errors with players data", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun checkPlayersScoreRTDB(){
        val database = Firebase.database.reference
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("ranking").exists()){
                    val players = ArrayList<Player>()
                    for (player in dataSnapshot.child("ranking").children){
                        players.add(player.getValue<Player>() as Player)

                    }
                    players.sortByDescending { it.huntedDucks }
                    //Poblar en RecyclerView información usando mi adaptador
                    val recyclerViewRanking: RecyclerView = findViewById(R.id.recyclerViewRanking);
                    recyclerViewRanking.layoutManager = LinearLayoutManager(this@RankingActivity);
                    recyclerViewRanking.adapter = RankingAdapter(players);
                    recyclerViewRanking.setHasFixedSize(true);
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)
    }
}