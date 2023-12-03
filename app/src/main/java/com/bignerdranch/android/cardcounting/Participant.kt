package com.bignerdranch.android.cardcounting

abstract class Participant : CardHolder() {
    // dealer and player both share methods here
    // like hand values, hitting, standing, going bust
    private var handValue: Int = 0
    private var handAces: Int = 0
    var hitReady: Boolean = true
    var handWin: Boolean = false
    var handBust: Boolean = false

    fun initHand() {
        // remove previous hand
        // deal two cards
        // update handValue
    }

    fun hitHand() {
        // check if newCard is ace, increment handAces if true
    }

    fun standHand() = apply {hitReady = false}

    fun checkBust() : Boolean {
        var bust = false
        if (handValue > 21) {
            if (handAces == 0) {
                bust = true
            } else {
                handAces--
                handValue -= 10
            }
        }

        return bust
    }

    fun setHandWin(win: Boolean) = apply {handWin = win}

    fun getHandValue() : Int {
        return handValue
    }
}