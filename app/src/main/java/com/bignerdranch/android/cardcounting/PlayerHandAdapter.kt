package com.bignerdranch.android.cardcounting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.cardcounting.databinding.CardViewBinding

class CardViewHolder(
    private val binding: CardViewBinding
) : RecyclerView.ViewHolder(binding.root){
    fun bind(card: Card) {
        binding.rankSymbolTop.text = card.rank.value.toString()
        binding.rankSymbolMiddle.text = card.rank.value.toString()
        binding.rankSymbolBottom.text = card.rank.value.toString()

        binding.suitSymbolTop.text = card.suit.symbol.toString()
        binding.suitSymbolBottom.text = card.suit.symbol.toString()
    }
}
class PlayerHandAdapter(
    private val cardList: MutableList<Card>
) : RecyclerView.Adapter<CardViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardViewBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.bind(card)
    }

    override fun getItemCount() = cardList.size
}