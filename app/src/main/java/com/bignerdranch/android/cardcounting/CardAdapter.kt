package com.bignerdranch.android.cardcounting

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.cardcounting.databinding.BlackjackCardViewBinding

class CardAdapter(
    private val context: Context,
    private val cardList: List<Card>,
    private val isDealer: Boolean
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: BlackjackCardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            // Customize this based on your CardView or layout structure
            binding.rankSymbolTop.text = card.rank.symbol
            binding.rankSymbolMiddle.text = card.rank.symbol
            binding.rankSymbolBottom.text = card.rank.symbol
            binding.suitSymbolTop.text = card.suit.symbol.toString()
            binding.suitSymbolBottom.text = card.suit.symbol.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = BlackjackCardViewBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.bind(card)
    }

    override fun getItemCount() = cardList.size
}
