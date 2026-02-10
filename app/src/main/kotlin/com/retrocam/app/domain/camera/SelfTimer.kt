package com.retrocam.app.domain.camera

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class TimerState {
    object Idle : TimerState()
    data class Counting(val secondsRemaining: Int) : TimerState()
    object Complete : TimerState()
}

enum class TimerDuration(val seconds: Int, val label: String) {
    OFF(0, "Off"),
    THREE(3, "3s"),
    FIVE(5, "5s"),
    TEN(10, "10s")
}

@Singleton
class SelfTimer @Inject constructor() {
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _selectedDuration = MutableStateFlow(TimerDuration.OFF)
    val selectedDuration: StateFlow<TimerDuration> = _selectedDuration.asStateFlow()

    fun setDuration(duration: TimerDuration) {
        _selectedDuration.value = duration
    }

    suspend fun startTimer(
        duration: TimerDuration = _selectedDuration.value,
        onTick: (Int) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        if (duration == TimerDuration.OFF) {
            _timerState.value = TimerState.Complete
            onComplete()
            return
        }

        for (i in duration.seconds downTo 1) {
            _timerState.value = TimerState.Counting(i)
            onTick(i)
            delay(1000)
        }

        _timerState.value = TimerState.Complete
        onComplete()
        delay(300) // Brief pause before resetting
        _timerState.value = TimerState.Idle
    }

    fun cancel() {
        _timerState.value = TimerState.Idle
    }

    fun isActive(): Boolean {
        return _timerState.value is TimerState.Counting
    }
}
