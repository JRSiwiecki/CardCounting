package com.bignerdranch.android.cardcounting

/* Code modified from KdeL's KCards */
class Deck private constructor(): CardHolder() {
    /**
     * Deck Builder. Do we need this private constructor?
     */
    class Builder {

        /**
         * Default builds a standard 52-card deck
         */
        fun build(): Deck {
            val deck = Deck()
            deck.loadDeck()
            deck.shuffleDeck()
            return deck
        }

        /**
         * Builds a mega-deck from a specified amount of complete decks
         */
        fun build(decks: Int) : Deck {
            val deck = Deck()
            for (i in 0 until decks) {
                deck.loadDeck()
            }
            deck.shuffleDeck()
            return deck
        }
    }



    private fun loadDeck() {
        val suits = ArrayList<Suit>()

        for (suit in Suit.values())
            suits.add(suit)

        //Add all suits
        for (suit in suits)
            for (rank in Rank.values())
                addCard(Card(suit, rank))
    }

    private fun shuffleDeck() {
        cards.shuffle()
    }

    private fun clearDeck() {
        cards.clear()
    }

    /**
     * Deals one card at a time
     */
    fun dealCard() : Card? {
        if (cards.size == 0) {
            clearDeck()
            loadDeck()
            shuffleDeck()
        }

        return cards.removeFirstOrNull()
    }
}