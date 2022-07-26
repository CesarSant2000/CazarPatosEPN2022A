package com.santacruz.cesar.cazarpatossc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

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

    }
}