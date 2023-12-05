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
            intent.putExtra("NUMBER_OF_DECKS", binding.numberOfDecks.value)
            intent.putExtra("TIME_PER_CARD", binding.timePerCard.text.toString().toInt())
            intent.putExtra("CHALLENGE_TYPE", mapChallengeType(binding.challengeType.checkedRadioButtonId))
            startActivity(intent)
        }

        binding.numberOfDecks.maxValue = 4
        binding.numberOfDecks.minValue = 1
    }

    private fun mapChallengeType(radioButtonId: Int): ChallengeType {
        return when (radioButtonId) {
            R.id.easy_button -> ChallengeType.EASY
            R.id.normal_button -> ChallengeType.NORMAL
            R.id.hard_button -> ChallengeType.HARD
            else -> ChallengeType.EASY
        }
    }

}