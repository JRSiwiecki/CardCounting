package com.bignerdranch.android.cardcounting

import android.os.CountDownTimer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class PracticeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var questionsAnswered: Int = 0
    var questionsCorrect: Int = 0
    var totalCount: Int = 0
    var currentCount: Int = 0

    private lateinit var countdownTimer: CountDownTimer

    fun getScore() {

    }
}