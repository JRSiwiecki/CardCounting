package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.cardcounting.databinding.ActivityPlayScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class PlayScreenActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPlayScreenBinding

    private lateinit var moneyTextView: TextView

    private lateinit var dealerHandRecyclerView: RecyclerView
    private lateinit var playerHandRecyclerView: RecyclerView
    private var paused: Boolean = false

    private lateinit var dealerAdapter: CardAdapter
    private lateinit var playerAdapter: CardAdapter

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

        startBlackJack()
        setUpHandViews()
        updateCardViews()
    }

    private fun startBlackJack(){
        playScreenViewModel.startBlackJack()

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
                handData.aceCount -= 1
            } else{
                bustHand(handData)
                endHand(handData)
                DisplayToast("Bust!")
                Log.d("Blackjack", "Bust")
            }
        }
        Log.d("Blackjack", "The current hand value is:" + handData.value)
        updateCardViews()
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
            runBlocking {
                dealerTurn()
            }
        }
    }

    suspend fun dealerTurn(){
        Log.d("Blackjack", "Starting the dealers turn")

        if(playScreenViewModel.hands.isEmpty()){
            gameOver()
            return
        }

        while(playScreenViewModel.dealer.value < 17
                || (playScreenViewModel.dealer.value == 17 &&
                playScreenViewModel.dealer.aceCount > 0)) {
            playScreenViewModel.dealCard(playScreenViewModel.dealer)
        }

        if(playScreenViewModel.dealer.value > 21){
            for (hand in playScreenViewModel.hands){
                payoutHand(hand)
                delay(2000)
            }
        }else{
            for (hand in playScreenViewModel.hands){
                
              if(playScreenViewModel.dealer.value < hand.value) {
                    payoutHand(hand)
                
              } else if(playScreenViewModel.dealer.value == hand.value) {
                    playScreenViewModel.money += hand.bet
                    DisplayToast("Push!")

                    displayCurrentMoney()
              } else {
                    DisplayToast("Lose!")
              }
                delay(2000)
            }
        }

        gameOver()
    }

    private fun payoutHand(handData: HandData){
        playScreenViewModel.money += handData.bet * 2
        DisplayToast("Win!")
        displayCurrentMoney()
    }


    private fun gameOver(){
        Log.d("Blackjack", "Hand is over")
        returnToBetting()
    }

    private fun displayCurrentMoney() {
        moneyTextView.text = "Money: $${String.format("%.2f", playScreenViewModel.money)}"
    }

    private fun DisplayToast(message: String){
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()

        runBlocking {
            PauseInput(Toast.LENGTH_SHORT)
        }

    }

    suspend fun PauseInput(delay: Int){
        paused = true
        delay(delay.toLong())
        paused = false
    }
    
    private fun returnToBetting(){
        val intent = Intent(this, PlayActivity::class.java)

        // Pass the necessary data to the next activity
        intent.putExtra("money", playScreenViewModel.money)

        // Start the next activity
        startActivity(intent)
    }

    private fun setUpHandViews() {
        dealerHandRecyclerView = findViewById(R.id.dealerHandRecyclerView)
        playerHandRecyclerView = findViewById(R.id.playerHandRecyclerViewBottom)

        dealerHandRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        playerHandRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize adapters for both dealer and player
        dealerAdapter = CardAdapter(this, playScreenViewModel.dealer.cardList, isDealer = true)
        dealerHandRecyclerView.adapter = dealerAdapter

        playerAdapter = CardAdapter(this, playScreenViewModel.activeHand.cardList, isDealer = false)
        playerHandRecyclerView.adapter = playerAdapter
    }

    private fun updateCardViews() {
        // Worst case scenario, go back to using .notifyDataSetChanged()
        dealerAdapter.notifyItemInserted(dealerAdapter.itemCount)
        playerAdapter.notifyItemInserted(playerAdapter.itemCount)
    }
}
