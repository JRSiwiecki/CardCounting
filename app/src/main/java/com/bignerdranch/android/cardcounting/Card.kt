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

enum class Rank(val symbol: String) {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");
}