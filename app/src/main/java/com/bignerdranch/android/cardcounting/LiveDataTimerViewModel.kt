package com.bignerdranch.android.cardcounting

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Timer
import java.util.TimerTask

/* Code modified from codelab-android-lifecycles */
class LiveDataTimerViewModel : ViewModel() {
    private val mElapsedTime = MutableLiveData<Long>()
    private val mInitialTime: Long = SystemClock.elapsedRealtime()
    private val timer: Timer = Timer()
    private val remainTime = MutableLiveData<Long>()

    init {

        // Update the elapsed time every second.
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val newValue = (SystemClock.elapsedRealtime() - mInitialTime) / 1000
                // setValue() cannot be called from a background thread so post to main thread.
                mElapsedTime.postValue(newValue)
            }
        }, ONE_SECOND.toLong(), ONE_SECOND.toLong())
    }

    val elapsedTime: LiveData<Long>
        get() = mElapsedTime

    fun cancel() {
        timer.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    companion object {
        private const val ONE_SECOND = 1000
    }
}