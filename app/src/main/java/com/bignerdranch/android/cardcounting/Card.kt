package com.bignerdranch.android.cardcounting

data class Card(val suit: Suit, val rank: Rank) {
    val display = "${rank.symbol}${suit.symbol}"
}

enum class Suit(val symbol: Char) {
    SPADES('\u2660'),
    HEARTS('\u2665'),
    DIAMONDS('\u2666'),
    CLUBS('\u2663');
}

enum class Rank(val symbol: String, val value: Int) {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 10),
    QUEEN("Q", 10),
    KING("K", 10),
    ACE("A", 11);
}