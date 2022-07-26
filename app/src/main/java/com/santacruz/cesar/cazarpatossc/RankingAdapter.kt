package com.santacruz.cesar.cazarpatossc

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(private val dataSet: ArrayList<Player>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private val TYPE_HEADER : Int = 0
    class ViewHolderHeader(view : View) : RecyclerView.ViewHolder(view){
        val textViewPosition: TextView = view.findViewById(R.id.textViewPosition)
        val textViewHuntedDucks: TextView = view.findViewById(R.id.textViewHuntedDucks)
        val textViewRankingUser: TextView = view.findViewById(R.id.textViewRankingUser)
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewPosition: TextView
        val textViewHuntedDucks: TextView
        val textViewRankingUser: TextView
        init {
            textViewPosition = view.findViewById(R.id.textViewPosition)
            textViewHuntedDucks = view.findViewById(R.id.textViewHuntedDucks)
            textViewRankingUser = view.findViewById(R.id.textViewRankingUser)
        }
    }
    override fun getItemViewType(position: Int): Int {
        if(position == 0){
            return TYPE_HEADER
        }
        return 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_HEADER){
            val header = LayoutInflater.from(parent.context).inflate(R.layout.ranking_list,parent,false)
            return ViewHolderHeader(header)
        }
        val header = LayoutInflater.from(parent.context).inflate(R.layout.ranking_list,parent,false)
        return ViewHolder(header)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderHeader){
            holder.textViewPosition.text = "#"
            holder.textViewPosition.paintFlags = holder.textViewPosition.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.textViewPosition.setTextColor(holder.textViewPosition.context.getColor( R.color.colorPrimaryDark))
            holder.textViewHuntedDucks.paintFlags = holder.textViewHuntedDucks.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.textViewHuntedDucks.setTextColor(holder.textViewHuntedDucks.context.getColor( R.color.colorPrimaryDark))
            holder.textViewRankingUser.paintFlags = holder.textViewRankingUser.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.textViewRankingUser.setTextColor(holder.textViewRankingUser.context.getColor( R.color.colorPrimaryDark))
        }
        else if (holder is ViewHolder) {
            holder.textViewPosition.text = position.toString()
            holder.textViewHuntedDucks.text = dataSet[position-1].huntedDucks.toString()
            holder.textViewRankingUser.text = dataSet[position-1].user
        }
    }
    override fun getItemCount() = dataSet.size + 1
}
