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

        binding.practiceModeButton.setOnClickListener {
            Log.d(TAG, "practice mode")
        }

        binding.hardModeButton.setOnClickListener {
            Log.d(TAG, "hard mode")
        }
    }
}
