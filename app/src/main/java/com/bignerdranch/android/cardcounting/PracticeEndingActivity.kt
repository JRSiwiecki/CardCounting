package com.bignerdranch.android.cardcounting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityPracticeEndingBinding

class PracticeEndingActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPracticeEndingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeEndingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data passed from PracticeActivity
        val finalScore = intent.getIntExtra("FINAL_SCORE", 0)
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        val correctCount = intent.getIntExtra("CORRECT_COUNT", 0)

        // Display the results in TextViews
        binding.answerCount.text = correctAnswers.toString()
        binding.finalCount.text = finalScore.toString()
        binding.correctCount.text = correctCount.toString()

        // Set up the button click listener
        binding.returnButton.setOnClickListener {
            // Handle the button click, you can return to the menu
             val intent = Intent(this, MainActivity::class.java)
             startActivity(intent)
            finish() // Finish the ResultActivity
        }
    }
}