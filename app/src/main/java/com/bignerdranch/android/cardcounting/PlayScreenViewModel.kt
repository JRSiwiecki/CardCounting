package com.bignerdranch.android.cardcounting

import android.util.Log
import androidx.lifecycle.ViewModel

data class HandData(
    var bet: Float,
    var cardList: MutableList<Card> = mutableListOf(),
    var value: Int = 0,
    var aceCount: Int = 0,
)
class PlayScreenViewModel : ViewModel() {
    var money: Float = 0f
    var hands: MutableList<HandData> = mutableListOf()
    var activeHandIndex: Int = 0

    lateinit var deck: Deck
    lateinit var betAmounts: MutableList<Float>
    lateinit var dealer: HandData
    lateinit var activeHand: HandData

    fun dealCard(handData: HandData){
        val newCard = deck.dealCard() ?: Card(Suit.SPADES, Rank.TWO)
        handData.cardList.add(newCard)
        handData.value += newCard.rank.value
        if(newCard.rank.value == 11){
            handData.aceCount ++
        }
        Log.d("Blackjack", "The card is a " + newCard.rank.name)
    }

    fun startBlackJack(){
        for(value in betAmounts){
            if(value > 0){
                hands.add(HandData(bet = value))
            }
        }

        dealer = HandData(bet = 0f)

        // Deal first set of cards (1st to each player)
        for(hand in hands){
            dealCard(hand)
        }

        dealCard(dealer)

        // Deal second set of cards (2nd to each player)
        for(hand in hands){
            dealCard(hand)
        }

        dealCard(dealer)

        //Pay out all blackjack players
        for(hand in hands){
            if(hand.value == 21){
                money += if(dealer.value < 21){
                    (hand.bet * 2.5f)
                }else{
                    (hand.bet)
                }
                //clearHand(hand)
            }
        }

        if(dealer.value == 21){
            for (hand in hands){
                //clearHand(hand)
            }

            //End game
            //gameOver()
        }

        activeHandIndex = 0
        activateHand(hands[0])

    }

    private fun activateHand(handData: HandData){
        activeHand = handData
    }

    fun hit(){
        Log.d("Blackjack", "Hit")
        dealCard(activeHand)
    }

    fun double(){
        Log.d("Blackjack", "Double")
        //Theoretically all options but stay will be closed,
        // forcing them to stay without recycling any logic
        //toggleButton(binding.hit, false)

        //money -= activeHand.bet
        money -=
            hands[activeHandIndex].bet

        //activeHand.bet *= 2
        hands[activeHandIndex].bet *= 2

        //dealCard(activeHand)
        dealCard(hands[activeHandIndex])
        //updateHand(activeHand)
    }

    fun clearHand() {
        //remove hand from table and list
        hands.removeAt(activeHandIndex)
        activeHandIndex--

        if (activeHandIndex < 0){
            activeHandIndex = 0
        }
    }

    fun endHand(): Boolean {
        //if theres another hand    activate the next hand
        //else dealer's turn
        activeHandIndex++

        return if(activeHandIndex < hands.size){
            activateHand(hands[activeHandIndex])
            false
        } else {

            activeHandIndex = 0
            true
        }
    }

}

