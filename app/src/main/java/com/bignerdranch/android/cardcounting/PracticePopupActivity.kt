package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityPracticePopupBinding

class PracticePopupActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPracticePopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticePopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val continueCount = intent.getIntExtra("CORRECT_COUNT", 0)

        binding.continueFinalButton.setOnClickListener{
            val resultIntent = Intent(this, PracticeEndingActivity::class.java)

            val userCount = binding.countInput.text.toString().toIntOrNull() ?: 0

            resultIntent.putExtra("FINAL_SCORE", userCount)
            resultIntent.putExtra("CORRECT_COUNT", continueCount)
            resultIntent.putExtra("CHALLENGE_TYPE", ChallengeType.HARD)

            startActivity(resultIntent)
        }

    }

}
