package com.bignerdranch.android.cardcounting

/* Code modified from KdeL's KCards */
class Deck private constructor() {
    private val cards = ArrayDeque<Card>()

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

    private fun addCard(card: Card) = cards.add(card)

    private fun loadDeck() {
        // Clear existing cards
        cards.clear()

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

    /**
     * Currently only deals one card at a time
     * Do we want to eventually deal more than one card at a time?
     */
    fun dealCard() : Card? {
        if (cards.size == 0) {
            loadDeck()

            // shuffle deck after loading,
            shuffleDeck()
        }

        return cards.removeFirstOrNull()
    }
}