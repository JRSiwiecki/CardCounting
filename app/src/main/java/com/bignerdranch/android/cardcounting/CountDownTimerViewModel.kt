package com.bignerdranch.android.cardcounting

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountDownTimerViewModel: ViewModel() {
    private lateinit var countdownTimer: CountDownTimer
    var timeRemainingString : MutableLiveData<String> = MutableLiveData()

    inner class Builder {
        fun build(timeMills: Long): CountDownTimerViewModel {
            val timer = CountDownTimerViewModel()
            timer.startCountdown(timeMills)
            return timer
        }
    }

    fun startCountdown(duration: Long) {
        countdownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = 1 + millisUntilFinished / 1000
                timeRemainingString.value = secondsRemaining.toString()
            }

            override fun onFinish() {
                //Failed to put count in time/automatically move to next card
            }
        }

        countdownTimer.start()
    }

    fun cancel() {
        countdownTimer.cancel()
    }

    fun start() {
        countdownTimer.start()
    }
}