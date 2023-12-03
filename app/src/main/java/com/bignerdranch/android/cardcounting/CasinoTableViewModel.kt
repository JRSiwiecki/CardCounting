package com.bignerdranch.android.cardcounting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class CasinoTableViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var playerMoney: Int = 0
}