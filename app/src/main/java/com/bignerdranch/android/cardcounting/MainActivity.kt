package com.bignerdranch.android.cardcounting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bignerdranch.android.cardcounting.databinding.ActivityMainBinding

private const val TAG = "Main"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            Log.d(TAG, "start game")
        }

        binding.instructionsButton.setOnClickListener {
            Log.d(TAG, "instructions")
        }
    }
}
