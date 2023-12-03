package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityBettingBinding
import com.bignerdranch.android.cardcounting.databinding.ActivityPlayScreenBinding

data class HandData(
    var bet: Float,
    var cardList: List<String>? = null,
    var value: Int = 0,
    var aceCount: Int = 0,
)

class PlayScreenActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPlayScreenBinding

    private var money: Float = 0f
    private lateinit var moneyTextView: TextView
    private lateinit var betAmounts: MutableList<Float>

    private var hands: MutableList<HandData> = mutableListOf()  // Initialize the list
    private lateinit var dealer: HandData

    private lateinit var activeHand: HandData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        activateHand(hands[0])
    }

    private fun activateHand(handData: HandData){
        toggleButton(binding.hit, true)
        toggleButton(binding.stay, true)
        if(money >= handData.bet) {
            toggleButton(binding.doubleDown, true)
        } else{
            toggleButton(binding.doubleDown, false)
        }
        //if(handData.card 1 == handData.card && money >= handData.bet 2) toggleButton(binding.split, true) else toggleButton(binding.split, false)
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
            }
        }
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

    }

    private fun stand(){
        endHand(activeHand)
    }

    private  fun hit(){
        dealCard(activeHand)
        updateHand(activeHand)
    }

    private fun double(){
        //Theoretically all options but stay will be closed, forcing them to stay without recycling any logic
        toggleButton(binding.hit, false)

        dealCard(activeHand)
        updateHand(activeHand)


    }

    private fun split(){
        //create a new hand with one of the card, deal a new card to each of those hands, and then continue with this hand
        //technically not the same card order but really doesnt matter
    }

    private fun clearHand(handData: HandData){
        //remove hand from table and list
    }

    private fun bustHand(handData: HandData){
        clearHand(handData)
    }

    private fun endHand(handData: HandData){
        //if theres another hand    activate the next hand
        //else dealerturn

    }

    private fun dealerTurn(){
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