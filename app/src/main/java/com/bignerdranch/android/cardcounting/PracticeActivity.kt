package com.bignerdranch.android.cardcounting

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.cardcounting.databinding.ActivityPracticeBinding

class PracticeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPracticeBinding
    private var count: Int = 0
    private lateinit var countdownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCountdown(5000)

        updateCountText()

        binding.pluscount.setOnClickListener { next(1) }
        binding.minuscount.setOnClickListener { next(-1) }
        binding.nocount.setOnClickListener { next(0) }
    }

    private fun startCountdown(duration: Long) {
        countdownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = 1 + millisUntilFinished / 1000
                binding.timer.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                binding.timer.text = "Countdown Finished!"
                //Failed to put count in time/automatically move to next card
            }
        }

        countdownTimer.start()
    }

    private fun next(delta: Int) {

        countdownTimer.cancel()

        count += delta

        updateCountText()

        startCountdown(5000)

        //Can add card logic/switches here
    }

    private fun updateCountText() {
        binding.count.text = "Count: $count"
    }

}