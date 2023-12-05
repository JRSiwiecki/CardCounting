package com.bignerdranch.android.cardcounting

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.android.cardcounting.databinding.ActivityPracticeBinding

class PracticeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPracticeBinding
    private lateinit var deck: Deck
    private lateinit var countdownTimer: CountDownTimerViewModel
    private lateinit var activeCard: Card
    private lateinit var cardView: CardView
    private lateinit var defaultColor: ColorStateList
    private lateinit var correctColor: ColorStateList
    private lateinit var disabledColor: ColorStateList

    // Score variables
    private var count: Int = 0
    private var cardsShown: Int = 0
    private var cardsCorrect: Int = 0

    // Settings variables
    private var numberOfDecks: Int = 1
    private var timePerCardMillis: Long = 5000
    private var challengeType: ChallengeType = ChallengeType.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        numberOfDecks = intent.getIntExtra("NUMBER_OF_DECKS", 1)
        var timePerCardSeconds = intent.getIntExtra("TIME_PER_CARD", 5)
        challengeType = intent.getSerializableExtra("CHALLENGE_TYPE") as ChallengeType
            ?: ChallengeType.EASY

        timePerCardMillis = timePerCardSeconds.toLong() * 1000

        defaultColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
        correctColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
        disabledColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray))

        deck = Deck.Builder().build()

        cardView = findViewById(R.id.activecard)

        updateActiveCard()

        countdownTimer = ViewModelProvider(this)[CountDownTimerViewModel::class.java]

        startCountdown(timePerCardMillis)

        updateCountText()

        binding.pluscount.setOnClickListener { next(1) }
        binding.minuscount.setOnClickListener { next(-1) }
        binding.nocount.setOnClickListener { next(0) }

        binding.nextCard.setOnClickListener {
            disableNextCardButton()
            updateActiveCard()
            resetCountButtons()
            startCountdown(timePerCardMillis)
        }

        countdownTimer.onTimerFinish = {
            // Player ran out of time, gave no answer (so use -2 as "no answer")
            evaluatePlayerAnswer(true, -2)
        }
    }

    private fun startCountdown(duration: Long) {
        countdownTimer.startCountdown(duration)

        // Observe the time remaining LiveData to update your UI
        countdownTimer.timeRemainingString.observe(this) { timeRemaining ->
            binding.timer.text = timeRemaining
        }
    }

    private fun next(delta: Int) {

        countdownTimer.cancel()

        count += delta

        updateCountText()

        startCountdown(5000)

        evaluatePlayerAnswer(false, delta)
    }

    private fun updateCountText() {
        binding.count.text = "Count: $count"
    }

    /**
     * Updates activeCard using getCardFromDeck,
     * updates cardView accordingly,
     * increments cardsShown.
     */
    private fun updateActiveCard() {
        // Get next card from deck
        activeCard = getCardFromDeck()

        // Update custom view
        cardView.setSymbols(
            activeCard.rank.symbol,
            activeCard.suit.symbol.toString(),
            activeCard.rank.symbol,
            activeCard.suit.symbol.toString()
        )

        cardsShown += 1
    }

    /**
     * Returns a new card from the deck, or the placeholder 2 of Spades.
     *
     * @return A variable of type Card.
     */
    private fun getCardFromDeck(): Card {
        return deck.dealCard() ?: Card(Suit.SPADES, Rank.TWO)
    }

    /**
     * Evaluates player answer and updates UI accordingly.
     * Tracks if userIsCorrect to increment cardsCorrect, updates timer to display either:
     * "Time Over" if user ran out of time, or "Correct!" or "Incorrect!" depending on if user
     * got the count right. Disables count buttons, and changes their background tints depending
     * on if user was correct or not. Also enables and makes nextCard button visible.
     *
     * @param ranOutOfTime Boolean, used to update timer text depending on if user answered in time or not.
     * @param answer Int, used to track how the user answered for the current card.
     */
    private fun evaluatePlayerAnswer(ranOutOfTime: Boolean, answer: Int) {
        var correctAnswer = 0
        var userIsCorrect = false

        // Evaluate if user answer was correct or not
        when (activeCard.rank.value) {
            in 2..6 -> {
                userIsCorrect = (answer == 1)
                correctAnswer = 1
            }
            in 7..9 -> {
                userIsCorrect = (answer == 0)
                correctAnswer = 0
            }
            else -> {
                userIsCorrect = (answer == -1)
                correctAnswer = -1
            }
        }

        binding.timer.text = if (ranOutOfTime) "Timer Over!" else if (userIsCorrect)  "Correct!" else "Incorrect!"

        // Keep track of number of correct answers
        if (userIsCorrect) {
            cardsCorrect += 1
        }

        // Disable count buttons
        binding.pluscount.isEnabled = false
        binding.nocount.isEnabled = false
        binding.minuscount.isEnabled = false

        // Set disabled color for all buttons
        binding.minuscount.backgroundTintList = disabledColor
        binding.nocount.backgroundTintList = disabledColor
        binding.pluscount.backgroundTintList = disabledColor

        // Highlight correct button green
        when (correctAnswer) {
            -1 -> binding.minuscount.backgroundTintList = correctColor
            0 -> binding.nocount.backgroundTintList = correctColor
            1 -> binding.pluscount.backgroundTintList = correctColor
        }

        // Display and enable nextCard button
        binding.nextCard.isVisible = true
        binding.nextCard.isEnabled = true
    }

    /**
     * Disables the button for pulling the next card.
     */
    private fun disableNextCardButton() {
        binding.nextCard.isVisible = false
        binding.nextCard.isEnabled = false
    }

    /**
     * Re-enable buttons, changes their background colors back to default (purple).
     */
    private fun resetCountButtons() {
        binding.pluscount.isEnabled = true
        binding.nocount.isEnabled = true
        binding.minuscount.isEnabled = true

        binding.pluscount.backgroundTintList = defaultColor
        binding.nocount.backgroundTintList = defaultColor
        binding.minuscount.backgroundTintList = defaultColor
    }
}