package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.cardcounting.databinding.ActivityPlayScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayScreenActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPlayScreenBinding

    private lateinit var moneyTextView: TextView

    private var paused: Boolean = false

    // Player hands
    private lateinit var playerHandRecyclerViewBottom: RecyclerView
    private lateinit var playerHandRecyclerViewMiddle: RecyclerView
    private lateinit var playerHandRecyclerViewTop: RecyclerView

    private lateinit var playerAdapterBottom: CardAdapter
    private lateinit var playerAdapterMiddle: CardAdapter
    private lateinit var playerAdapterTop: CardAdapter

    // Dealer hands
    private lateinit var dealerHandRecyclerView: RecyclerView
    private lateinit var dealerAdapter: CardAdapter

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

    private fun updateHand(handData: HandData){
        toggleButton(binding.doubleDown, false)

        updateCardViews()

        if(handData.value > 21){
            if(handData.aceCount > 0){
                handData.value -= 10
                handData.aceCount -= 1
            } else{
                bustHand()
                endHand()
                displayToast("Bust!")
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

        updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])
    }

    private fun stand(){
        Log.d(
            "Blackjack",
            "Standing at: "
                    + playScreenViewModel.hands[playScreenViewModel.activeHandIndex].value)
        endHand()

        updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])
    }

    private fun double(){
        playScreenViewModel.double()
        //Theoretically all options but stay will be closed,
        // forcing them to stay without recycling any logic
        toggleButton(binding.hit, false)

        displayCurrentMoney()

        updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])
    }

    private fun bustHand(){
        playScreenViewModel.clearHand()

        if (playScreenViewModel.activeHandIndex < playScreenViewModel.hands.size) {
            updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])
        } else {
            Log.d("BJ", "LEAVE")
            gameOver()
        }

    }

    private fun endHand() {
        //if theres another hand    activate the next hand
        //else dealer's turn
        val endPlayerTurn = playScreenViewModel.endHand()

        // updateHand(playScreenViewModel.hands[playScreenViewModel.activeHandIndex])

        if (endPlayerTurn) {
            lifecycleScope.launch {
                dealerTurn()
            }
        }
    }

    private suspend fun dealerTurn(){
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

        withContext(Dispatchers.Main) {
            updateDealerCards()
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
                  displayToast("Push!")

                    displayCurrentMoney()
              } else {
                  displayToast("Lose!")
              }
                delay(2000)
            }
        }

        gameOver()
    }

    private fun payoutHand(handData: HandData){
        playScreenViewModel.money += handData.bet * 2
        displayToast("Win!")
        displayCurrentMoney()
    }

    private fun gameOver(){
        Log.d("Blackjack", "Hand is over")
        returnToBetting()
    }

    private fun displayCurrentMoney() {
        moneyTextView.text = "Money: $${String.format("%.2f", playScreenViewModel.money)}"
    }

    private fun displayToast(message: String){
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()

        lifecycleScope.launch {
            pauseInput(Toast.LENGTH_SHORT)
        }

    }

    private suspend fun pauseInput(delay: Int){
        paused = true
        delay(delay.toLong())
        paused = false
    }
    
    private fun returnToBetting(){
        val intent = Intent(this, PlayActivity::class.java)

        // Pass the necessary data to the next activity
        intent.putExtra("money", playScreenViewModel.money)

        // End this activity to prevent crashes
        finish()

        // Start the next activity
        startActivity(intent)
    }

    private fun setUpHandViews() {
        // Set up main dealer and player hands
        dealerHandRecyclerView = findViewById(R.id.dealerHandRecyclerView)
        playerHandRecyclerViewBottom = findViewById(R.id.playerHandRecyclerViewBottom)

        dealerHandRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        playerHandRecyclerViewBottom.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize adapters for both dealer and player
        dealerAdapter = CardAdapter(this, playScreenViewModel.dealer.cardList, isDealer = true)
        dealerHandRecyclerView.adapter = dealerAdapter

        playerAdapterBottom = CardAdapter(this, playScreenViewModel.hands[0].cardList, isDealer = false)
        playerHandRecyclerViewBottom.adapter = playerAdapterBottom

        // Set up extra player hands if needed
        playerHandRecyclerViewMiddle = findViewById(R.id.playerHandRecyclerViewMiddle)
        playerHandRecyclerViewTop = findViewById(R.id.playerHandRecyclerViewTop)

        // Display each one that is needed
        when (playScreenViewModel.betAmounts.size) {
            2 -> {
                playerAdapterMiddle = CardAdapter(this, playScreenViewModel.hands[1].cardList, isDealer = false)
                playerHandRecyclerViewMiddle.adapter = playerAdapterMiddle

                playerHandRecyclerViewMiddle.isVisible = true
            }

            3 -> {
                playerAdapterMiddle = CardAdapter(this, playScreenViewModel.hands[1].cardList, isDealer = false)
                playerHandRecyclerViewMiddle.adapter = playerAdapterMiddle

                playerAdapterTop = CardAdapter(this, playScreenViewModel.hands[2].cardList, isDealer = false)
                playerHandRecyclerViewTop.adapter = playerAdapterTop

                playerHandRecyclerViewMiddle.isVisible = true
                playerHandRecyclerViewTop.isVisible = true
            }
        }
    }

    private fun updateDealerCards() {
        dealerAdapter.notifyDataSetChanged()
    }

    private fun updateCardViews() {
        updateHandColors()

        playerAdapterBottom.notifyDataSetChanged()

        if (playScreenViewModel.betAmounts.size >= 2) {
            playerAdapterMiddle.notifyDataSetChanged()
        }

        if (playScreenViewModel.betAmounts.size >= 3) {
            playerAdapterTop.notifyDataSetChanged()
        }
    }

    private fun updateHandColors() {
        playerHandRecyclerViewBottom.setBackgroundColor(getBackgroundColor(0))
        playerHandRecyclerViewMiddle.setBackgroundColor(getBackgroundColor(1))
        playerHandRecyclerViewTop.setBackgroundColor(getBackgroundColor(2))
    }

    private fun getBackgroundColor(handIndex: Int): Int {
        return if (handIndex == playScreenViewModel.activeHandIndex) {
            // Use the highlighted color for the active hand
            ContextCompat.getColor(this, R.color.green)
        } else {
            // Use the default background color for non-active hands
            ContextCompat.getColor(this, android.R.color.transparent)
        }
    }
}
