package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityBettingBinding
import com.bignerdranch.android.cardcounting.databinding.ActivityPlayScreenBinding

data class HandData(
    var bet: Float,
    var cardList: MutableList<Card> = mutableListOf(),
    var value: Int = 0,
    var aceCount: Int = 0,
)

class PlayScreenActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPlayScreenBinding

    private var money: Float = 0f
    private lateinit var moneyTextView: TextView
    private lateinit var betAmounts: MutableList<Float>

    private lateinit var dealerCard1: CardView
    private lateinit var dealerCard2: CardView
    private lateinit var dealerCard3: CardView
    private lateinit var dealerCard4: CardView
    private lateinit var dealerCard5: CardView
    private lateinit var dealerCard6: CardView
    private lateinit var dealerCard7: CardView
    private lateinit var dealerCard8: CardView

    private var hands: MutableList<HandData> = mutableListOf()  // Initialize the list
    private lateinit var dealer: HandData

    private lateinit var activeHand: HandData
    private var activeHandIndex: Int = 0

    private lateinit var deck: Deck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deck = Deck.Builder().build()

        // Retrieve data from the intent
        money = intent.getFloatExtra("money", 0f) // 0f is the default value if the key is not found
        val betAmountsArray = intent.getFloatArrayExtra("betAmounts")
        betAmounts = betAmountsArray?.toMutableList() ?: mutableListOf()

        moneyTextView = binding.gameMoney
        // Now you can use 'money' and 'betAmounts' in your ActivityPlayScreen
        displayCurrentMoney()

        binding.tempReturn.setOnClickListener {
            returnToBetting()
        }

        binding.hit.setOnClickListener {
            hit()
        }

        binding.stay.setOnClickListener {
            stand()
        }

        binding.doubleDown.setOnClickListener {
            double()
        }

        binding.split.setOnClickListener {
            split()
        }

        startBlackJack()




    }

    private fun startBlackJack(){
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
                if(dealer.value < 21){
                    money += (hand.bet * 2.5f)
                    displayCurrentMoney()
                }else{
                    money += (hand.bet)
                    displayCurrentMoney()
                }
                clearHand(hand)
            }
        }

        if(dealer.value == 21){
            for(hand in hands){
                clearHand(hand)
            }

            //End game
            gameOver()
        }

        activeHandIndex = 0
        activateHand(hands[0])

    }

    private fun activateHand(handData: HandData){
        activeHand = handData
        activeHand = hands[activeHandIndex]

        toggleButton(binding.hit, true)
        toggleButton(binding.stay, true)
        if(money >= handData.bet) {
            toggleButton(binding.doubleDown, true)
        } else{
            toggleButton(binding.doubleDown, false)
        }
        if(handData.cardList[0].rank == handData.cardList[1].rank && money >= handData.bet) {
            toggleButton(binding.split, true)
        } else {
            toggleButton(binding.split, false)
        }
        Log.d("Blackjack", "The starting hand value is:" + handData.value)
    }

    private fun updateHand(handData: HandData){
        toggleButton(binding.doubleDown, false)
        toggleButton(binding.split, false)

        if(handData.value > 21){
            if(handData.aceCount > 0){
                handData.value -= 10
                handData.aceCount += 10
            } else{
                bustHand(handData)
                endHand(handData)
                Log.d("Blackjack", "Bust")
            }
        }
        Log.d("Blackjack", "The current hand value is:" + handData.value)
    }

    private fun toggleButton(button: Button, enabled: Boolean){
        button.isEnabled = enabled
        if(enabled){
            button.alpha = 1f
        } else{
            button.alpha = .3f
        }
    }

    private fun dealCard(handData: HandData){
        val newCard = deck.dealCard() ?: Card(Suit.SPADES, Rank.TWO)
        handData.cardList.add(newCard)
        handData.value += newCard.rank.value
        if(newCard.rank.value == 11){
            handData.aceCount ++
        }
        Log.d("Blackjack", "The card is a " + newCard.rank.name)
    }

    private fun recalculateHandValue(handData: HandData){
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

    private fun stand(){
        Log.d("Blackjack", "Standing at: " + hands[activeHandIndex].value)
        endHand(activeHand)
    }

    private  fun hit(){
        Log.d("Blackjack", "Hit")
        dealCard(activeHand)
        updateHand(activeHand)
    }

    private fun double(){
        Log.d("Blackjack", "Double")
        //Theoretically all options but stay will be closed, forcing them to stay without recycling any logic
        toggleButton(binding.hit, false)

        //money -= activeHand.bet
        money -= hands[activeHandIndex].bet
        displayCurrentMoney()

        //activeHand.bet *= 2
        hands[activeHandIndex].bet *= 2

        //dealCard(activeHand)
        dealCard(hands[activeHandIndex])
        //updateHand(activeHand)
        updateHand(hands[activeHandIndex])

    }

    private fun split(){
        Log.d("Blackjack", "Split")
        //create a new hand with one of the card, deal a new card to each of those hands, and then continue with this hand
        //technically not the same card order but really doesnt matter

        money -= hands[activeHandIndex].bet
        displayCurrentMoney()

        val movedcard = hands[activeHandIndex].cardList[0]
        Log.d("Blackjack", "The split card is: " + movedcard.display)
        hands[activeHandIndex].cardList.removeAt(0)
        recalculateHandValue(hands[activeHandIndex])

        //Add a new hand to the list, and split the cards between the two
        hands.add(activeHandIndex, HandData(bet = hands[activeHandIndex].bet))

        hands[activeHandIndex].cardList.add(movedcard)
        recalculateHandValue(hands[activeHandIndex])

        dealCard(hands[activeHandIndex])
        updateHand(hands[activeHandIndex])
        Log.d("Blackjack", "The right hands size is + : " + hands[activeHandIndex].cardList.size)

        dealCard(hands[activeHandIndex + 1])
        updateHand(hands[activeHandIndex + 1])
        Log.d("Blackjack", "The right hands size is + : " + hands[activeHandIndex+1].cardList.size)

        activateHand(hands[activeHandIndex])

    }

    private fun clearHand(handData: HandData){
        //remove hand from table and list
        hands.removeAt(activeHandIndex)
        activeHandIndex --
    }

    private fun bustHand(handData: HandData){
        clearHand(handData)
    }

    private fun endHand(handData: HandData){
        //if theres another hand    activate the next hand
        //else dealerturn
        activeHandIndex ++
        if(activeHandIndex < hands.size){
            activateHand(hands[activeHandIndex])
        } else{
            dealerTurn()
        }
    }

    private fun dealerTurn(){

        if(hands.isEmpty()){
            gameOver()
            return
        }

        while(dealer.value < 17 || dealer.value == 17 && dealer.aceCount > 0){
            dealCard(dealer)
        }

        if(dealer.value > 21){
            for (hand in hands){
                payoutHand(hand)
            }
        }else{
            for (hand in hands){
                if(dealer.value < hand.value){
                    payoutHand(hand)
                }else if(dealer.value == hand.value){
                    money += hand.bet
                    displayCurrentMoney()
                }
            }
        }

        gameOver()
    }

    private fun payoutHand(handData: HandData){
        money += handData.bet * 2
        displayCurrentMoney()
    }


    private fun gameOver(){
        returnToBetting()
    }

    private fun displayCurrentMoney() {
        moneyTextView.text = "Money: $${String.format("%.2f", money)}"
    }

    private fun setButtonTextValue(button: Button, value: Float) {
        button.text = "${String.format("%.0f", value)}"
    }




    private fun returnToBetting(){
        val intent = Intent(this, PlayActivity::class.java)

        // Pass the necessary data to the next activity
        intent.putExtra("money", money)

        // Start the next activity
        startActivity(intent)
    }

}