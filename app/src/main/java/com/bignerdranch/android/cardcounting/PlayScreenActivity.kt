package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.cardcounting.databinding.ActivityPlayScreenBinding


class PlayScreenActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPlayScreenBinding

    private lateinit var moneyTextView: TextView

    private lateinit var dealerHandRecyclerView: RecyclerView
    private lateinit var playerHandRecyclerView: RecyclerView

    private lateinit var dealerHandAdapter: PlayerHandAdapter
    private lateinit var playerHandAdapter: PlayerHandAdapter

    private val playScreenViewModel: PlayScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playScreenViewModel.deck = Deck.Builder().build()

        // Retrieve data from the intent
        // 0f is the default value if the key is not found
        playScreenViewModel.money = intent.getFloatExtra("money", 0f)
        val betAmountsArray = intent.getFloatArrayExtra("betAmounts")
        playScreenViewModel.betAmounts = betAmountsArray?.toMutableList() ?: mutableListOf()

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

        setUpHandViews()
        startBlackJack()
    }

    private fun startBlackJack(){
        playScreenViewModel.startBlackJack()
        updateCardViews()

        //Pay out all blackjack players
        for(hand in playScreenViewModel.hands){
            if(hand.value == 21){
                displayCurrentMoney()
            }
        }
    }

    private fun activateHand(handData: HandData){
        playScreenViewModel.activateHand(handData)

        toggleButton(binding.hit, true)
        toggleButton(binding.stay, true)

        if(playScreenViewModel.money >= handData.bet) {
            toggleButton(binding.doubleDown, true)
        } else{
            toggleButton(binding.doubleDown, false)
        }

        if(handData.cardList[0].rank == handData.cardList[1].rank
                && playScreenViewModel.money >= handData.bet) {
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

    private fun hit(){
        playScreenViewModel.hit()
        updateHand(playScreenViewModel.activeHand)
        updateCardViews()
    }



    private fun stand(){
        Log.d(
            "Blackjack",
            "Standing at: "
                    + playScreenViewModel.hands[playScreenViewModel.activeHandIndex].value)
        endHand(playScreenViewModel.activeHand)
    }

    private fun double(){
        playScreenViewModel.double()
        //Theoretically all options but stay will be closed,
        // forcing them to stay without recycling any logic
        toggleButton(binding.hit, false)

        displayCurrentMoney()

        updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])
    }

    private fun split(){
        playScreenViewModel.split()

        activateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])

        displayCurrentMoney()

        updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])
        updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex + 1])
    }

    private fun bustHand(handData: HandData){
        playScreenViewModel.clearHand(handData)
    }

    private fun endHand(handData: HandData){
        //if theres another hand    activate the next hand
        //else dealer's turn
        val endPlayerTurn = playScreenViewModel.endHand(handData)
        if (endPlayerTurn) {
            dealerTurn()
        }
    }

    private fun dealerTurn(){

        if(playScreenViewModel.hands.isEmpty()){
            gameOver()
            return
        }

        while(playScreenViewModel.dealer.value < 17
                || playScreenViewModel.dealer.value == 17 &&
                playScreenViewModel.dealer.aceCount > 0){
            playScreenViewModel.dealCard(playScreenViewModel.dealer)
        }

        if(playScreenViewModel.dealer.value > 21){
            for (hand in playScreenViewModel.hands){
                payoutHand(hand)
            }
        }else{
            for (hand in playScreenViewModel.hands){
                if(playScreenViewModel.dealer.value < hand.value){
                    payoutHand(hand)
                }else if(playScreenViewModel.dealer.value == hand.value){
                    playScreenViewModel.money += hand.bet
                    displayCurrentMoney()
                }
            }
        }

        gameOver()
    }

    private fun payoutHand(handData: HandData){
        playScreenViewModel.money += handData.bet * 2
        displayCurrentMoney()
    }


    private fun gameOver(){
        returnToBetting()
    }

    private fun displayCurrentMoney() {
        moneyTextView.text = "Money: $${String.format("%.2f", playScreenViewModel.money)}"
    }

    private fun returnToBetting(){
        val intent = Intent(this, PlayActivity::class.java)

        // Pass the necessary data to the next activity
        intent.putExtra("money", playScreenViewModel.money)

        // Start the next activity
        startActivity(intent)
    }

    private fun setUpHandViews() {
        TODO("This block of code has player_hand_recycler_view return null which causes an exception")
        playerHandRecyclerView = findViewById(R.id.player_hand_recycler_view)
        dealerHandRecyclerView = findViewById(R.id.dealer_hand_recycler_view)

        playerHandRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        playerHandAdapter = PlayerHandAdapter(playScreenViewModel.activeHand.cardList)

        dealerHandRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dealerHandAdapter = PlayerHandAdapter(playScreenViewModel.dealer.cardList)
    }

    private fun updateCardViews() {
        TODO("Not yet implemented")
    }
}
