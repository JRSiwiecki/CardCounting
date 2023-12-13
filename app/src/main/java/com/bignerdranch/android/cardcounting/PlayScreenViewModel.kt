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

        for(hand in hands){
            dealCard(hand)
        }

        dealCard(dealer)

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
            for(hand in hands){
                //clearHand(hand)
            }

            //End game
            //gameOver()
        }

        activeHandIndex = 0
        activateHand(hands[0])

    }

    fun activateHand(handData: HandData){
        activeHand = handData
        activeHand =
            hands[activeHandIndex]
    }

    fun recalculateHandValue(handData: HandData){
        handData.value = 0
        handData.aceCount = 0
        for(card in handData.cardList){
            handData.value += card.rank.value
            if(card.rank.value == 11){
                handData.aceCount ++
            }

            if(handData.value > 21 && handData.aceCount > 0){
                handData.value -= 10
                handData.aceCount --
            }
        }
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

    fun split(){
        Log.d("Blackjack", "Split")
        //create a new hand with one of the card,
        // deal a new card to each of those hands, and then continue with this hand
        //technically not the same card order but really doesn't matter

        money -= hands[activeHandIndex].bet

        val movedCard = hands[activeHandIndex].cardList[0]
        Log.d("Blackjack", "The split card is: " + movedCard.display)
        hands[activeHandIndex].cardList.removeAt(0)
        recalculateHandValue(hands[activeHandIndex])

        //Add a new hand to the list, and split the cards between the two
        hands.add(activeHandIndex, HandData(bet = hands[activeHandIndex].bet))

        hands[activeHandIndex].cardList.add(movedCard)
        recalculateHandValue(hands[activeHandIndex])

        dealCard(hands[activeHandIndex])

        Log.d(
            "Blackjack",
            "The right hands size is + : "
                    + hands[activeHandIndex].cardList.size
        )

        dealCard(hands[activeHandIndex + 1])

        Log.d("Blackjack",
            "The right hands size is + : "
                    + hands[activeHandIndex+1].cardList.size
        )
    }

    fun clearHand(handData: HandData){
        //remove hand from table and list
        hands.removeAt(activeHandIndex)
        activeHandIndex--

        if (activeHandIndex < 0){
            activeHandIndex = 0
        }
    }

    fun bustHand(handData: HandData){
        clearHand(handData)
    }

    fun endHand(handData: HandData) : Boolean{
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

