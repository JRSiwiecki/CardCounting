package com.bignerdranch.android.cardcounting

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityBettingBinding

class PlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBettingBinding
    private var money: Float = 50.50f
    private lateinit var activeButton: Button
    private lateinit var moneyTextView: TextView

    private val betAmounts = mutableListOf(0f, 0f, 0f)

    // Map to associate bet buttons with their corresponding index in the list
    private val betButtonMap = mapOf(
        R.id.bet_1 to 0,
        R.id.bet_2 to 1,
        R.id.bet_3 to 2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBettingBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Assuming you have a TextView with id "moneyTextView" in your layout
        moneyTextView = binding.money

        // Initialize the display with the initial money value
        displayCurrentMoney()

        // Assuming you have a Button with id "bet_1" in your layout
        activeButton = binding.bet1
        setActiveButtonColor()

        setBetButtonListeners()
        setCountingButtonListeners()

        binding.startGame.setOnClickListener {
            startGame()
        }

        binding.acceptBet.setOnClickListener {
            lockInBet()
        }

    }

    private fun displayCurrentMoney() {
        moneyTextView.text = "Money: $${String.format("%.2f", money)}"
    }

    private fun setButtonTextValue(button: Button, value: Float) {
        button.text = "${String.format("%.0f", value)}"
    }

    private fun setBetButtonListeners() {
        val betButtons = listOf<Button>(findViewById(R.id.bet_1), findViewById(R.id.bet_2), findViewById(R.id.bet_3)/* add other buttons as needed */)

        for (button in betButtons) {
            button.setOnClickListener {

                switchActiveButton(button)
            }
        }
    }

    private fun switchActiveButton(newButton: Button){
        resetActiveButtonColor()
        setButtonTextValue(activeButton, betAmounts[betButtonMap[activeButton.id] ?: 0])
        //activeButton.text = betAmounts[betButtonMap[activeButton.id] ?: 0].toString()
        activeButton = newButton
        setActiveButtonColor()
    }

    private fun setCountingButtonListeners() {
        val bettingButtons = listOf<Button>(findViewById(R.id.add_10), findViewById(R.id.minus_10), /* add other buttons as needed */)

        for (button in bettingButtons) {
            button.setOnClickListener {
                val betAmount = button.text.toString().toInt()
                updateActiveButtonValue(betAmount)
            }
        }
    }

    private fun updateActiveButtonValue(betAmount: Int) {
        val newMoney = activeButton.text.toString().toFloat() + betAmount
        if ((betAmount > 0 && newMoney <= money) || (betAmount < 0 && newMoney >= 0)) {

            setButtonTextValue(activeButton, newMoney)
        }
    }

    private fun setActiveButtonColor() {
        activeButton.setBackgroundColor(0xFF00FF00.toInt()) // Green color in hexadecimal
    }

    private fun resetActiveButtonColor() {
        activeButton.setBackgroundColor(0xFFCCCCCC.toInt()) // Your default color in hexadecimal
    }

    private fun lockInBet(){
        val index = betButtonMap[activeButton.id] ?: 0
        betAmounts[index] = activeButton.text.toString().toFloat()
        money -= activeButton.text.toString().toFloat()
        displayCurrentMoney()
    }



    private fun startGame() {
        // Your start game logic here
    }
}
