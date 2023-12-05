package com.bignerdranch.android.cardcounting

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountDownTimerViewModel: ViewModel() {
    private lateinit var countdownTimer: CountDownTimer
    var timeRemainingString : MutableLiveData<String> = MutableLiveData()
    var onTimerFinish: (() -> Unit)? = null

    fun startCountdown(duration: Long) {
        countdownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = 1 + millisUntilFinished / 1000
                timeRemainingString.value = secondsRemaining.toString()
            }

            override fun onFinish() {
                timeRemainingString.value = "0"
                onTimerFinish?.invoke()
            }
        }

        countdownTimer.start()
    }

    fun cancel() {
        countdownTimer.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        countdownTimer.cancel()
    }
}