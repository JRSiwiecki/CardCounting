package com.bignerdranch.android.cardcounting

/* Code modified from KdeL's KCards */
/**
 * [CardHolder] to be extended by all entities holding [Card]s. Examples can be a deck of cards or a player.
 */
abstract class CardHolder {
    /**
     * List of [Card]s held by this holder at any given time. For a player, this may represent their "hand".
     * Members [addCard], [removeCard], [moveCard] and [moveAllCards] are used to add/remove [Card]s on this list.
     * Use [shuffle] to re-arrange the [Card]s into a random order.
     */
    protected val cards = ArrayList<Card>()

    /**
     * adds the [card] to the [cards] pile. Returns the CardHolder .
     * The [card] is appended to the end of [cards] pile.
     */
    protected infix fun addCard(card: Card) = apply {cards.add(card)}

    /**
     * Removes the [card] from the [cards] pile
     */
    protected infix fun removeCard(card: Card) = apply { cards.remove(card) }

    /**
     * Removes all cards from [cards] pile
     */
    protected fun removeAllCards() = apply { cards.clear() }

    /**
     * Removes the given [Card] from [cards] and adds it to the given [recipient].
     * If there are multiple cards matching the criteria ( [suit] and [rank] ),only  the first one found in [cards] is moved.
     * Return value indicates if the operation was successful.
     * New objects are not instantiated during this operation.
     */
    fun moveCard(suit: Suit, rank: Rank, recipient: CardHolder): Boolean {
        cards.firstOrNull { card -> card.suit == suit && card.rank == rank }?.let { card ->
            cards.remove(card)
            recipient.cards.add(card)
            return true
        }
        return false
    }

    /**
     * Moves [count] of [Card]s to the recipient. Returns if operation was successful
     */
    fun moveCards(recipient: CardHolder, count: Int = 1): Boolean{
        if(cards.size < count) return false
        for(i in 1..count) recipient.cards.add(cards.removeFirst())
        return true
    }

    /**
     * Moves all from [cards] to the [recipient]. New objects are not instantiated during this operation.
     */
    fun moveAllCards(recipient: CardHolder) {
        while(cards.isNotEmpty()) { recipient.cards.add(cards.removeFirst()) }
    }

    /**
     * Returns a complete list of [Card]s in [cards] as an immutable list
     */
    fun listOfCards(): List<Card> { return cards }
}