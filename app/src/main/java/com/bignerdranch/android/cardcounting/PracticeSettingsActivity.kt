package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityPracticeSettingsBinding

class PracticeSettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPracticeSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startPracticeButton.setOnClickListener {
            val intent = Intent(this, PracticeActivity::class.java)

            // Small check in case timePerCard was not entered by default
            val timePerCard = binding.timePerCard.text.toString().toIntOrNull() ?: 5

            intent.putExtra("NUMBER_OF_DECKS", binding.numberOfDecks.value)
            intent.putExtra("TIME_PER_CARD", timePerCard)
            intent.putExtra("CHALLENGE_TYPE", mapChallengeType(binding.challengeType.checkedRadioButtonId))
            startActivity(intent)
        }

        binding.numberOfDecks.maxValue = 8
        binding.numberOfDecks.minValue = 1
    }

    /**
     * Maps radio buttons to their corresponding ChallengeType.
     *
     * @param radioButtonId Int. Holds the ID for a corresponding Radio Button.
     * @return ChallengeType. Returns the corresponding ChallengeType of the radio button selected.
     */
    private fun mapChallengeType(radioButtonId: Int): ChallengeType {
        return when (radioButtonId) {
            R.id.easy_button -> ChallengeType.EASY
            R.id.normal_button -> ChallengeType.NORMAL
            R.id.hard_button -> ChallengeType.HARD
            else -> ChallengeType.EASY
        }
    }

}