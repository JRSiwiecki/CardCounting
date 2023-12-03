package com.bignerdranch.android.cardcounting

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityPracticeBinding

class PracticeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPracticeBinding
    private lateinit var deck: Deck
    private var count: Int = 0
    private lateinit var countdownTimer: CountDownTimerViewModel
    private lateinit var activeCard: CardView
    private val cardTimerDuration: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deck = Deck.Builder().build()

        activeCard = findViewById(R.id.activecard)

        updateActiveCard()

        startCountdown(5000)

        updateCountText()

        binding.pluscount.setOnClickListener { next(1) }
        binding.minuscount.setOnClickListener { next(-1) }
        binding.nocount.setOnClickListener { next(0) }
    }

    private fun startCountdown(duration: Long) {
        countdownTimer = CountDownTimerViewModel().Builder().build(cardTimerDuration)

        countdownTimer.start()

        fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = 1 + millisUntilFinished / 1000
            binding.timer.text = secondsRemaining.toString()
        }

        /**
         * countdownTimer = object : CountDownTimer(duration, 1000) {
         *             override fun onTick(millisUntilFinished: Long) {
         *                 val secondsRemaining = 1 + millisUntilFinished / 1000
         *                 binding.timer.text = secondsRemaining.toString()
         *             }
         *
         *             override fun onFinish() {
         *                 binding.timer.text = "Countdown Finished!"
         *                 //Failed to put count in time/automatically move to next card
         *             }
         *         }
         */

        // FIXME: Should probably be uncommented when countdownTimer is fixed...
        // updateActiveCard()
    }

    private fun next(delta: Int) {

        countdownTimer.cancel()

        count += delta

        updateCountText()

        startCountdown(5000)

        updateActiveCard()

        //Can add card logic/switches here
    }

    private fun updateCountText() {
        binding.count.text = "Count: $count"
    }

    private fun updateActiveCard() {

        // Get next card from deck
        val nextCard = getCardFromDeck()

        // Update custom view
        activeCard.setSymbols(
            nextCard.rank.symbol,
            nextCard.suit.symbol.toString(),
            nextCard.rank.symbol,
            nextCard.suit.symbol.toString()
        )
    }
    private fun getCardFromDeck(): Card {
        // Get card and return or return placeholder card
        return deck.dealCard() ?: Card(Suit.SPADES, Rank.TWO)
    }
}