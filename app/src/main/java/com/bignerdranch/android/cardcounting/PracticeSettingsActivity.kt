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
            startActivity(intent)
        }

        binding.numberOfDecks.maxValue = 4
        binding.numberOfDecks.minValue = 1
    }

}