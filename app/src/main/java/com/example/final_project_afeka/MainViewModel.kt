package com.example.final_project_afeka

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    arguments: SavedStateHandle,
) : ViewModel() {
    val endTime = MutableStateFlow<String?>(null)
    private val _drivingCounter = MutableStateFlow<Long?>(null)
    private val startTime = MutableStateFlow<Long?>(null)
    val drivingCounter = _drivingCounter.combine(startTime) { counter , start ->
        if(counter == null || start == null) null
        else counter.formatToTime(start)
    }

    fun startDriving() {
        startTime.value = System.currentTimeMillis()
    }

    init {
        initialTimerLogic()
    }

    private fun initialTimerLogic() {
        viewModelScope.launch {
            startTime.collect {
                _drivingCounter.value = it
            }
        }
        viewModelScope.launch {
            endTime.collect {
                _drivingCounter.value = null
                startTime.value = null
            }
        }
        viewModelScope.launch {
            _drivingCounter.collect {
                it?.let {
                    delay(1000)
                    _drivingCounter.value = it + 1000
                }
            }
        }
    }

    fun stopDriving() {
        val start = startTime.value ?:0
        println("startTime  ${startTime.value}")
        println("currentTimeMillis  ${System.currentTimeMillis()}")
        val end = System.currentTimeMillis() - start
        println("end  ${System.currentTimeMillis()}")
        endTime.value = end.formatToTime(start)
        println("stopDriving  ${endTime.value}")
    }

}

fun Long.formatToTime(startTime: Long = 0): String {
    val delta = System.currentTimeMillis() - startTime
    val hours = TimeUnit.MILLISECONDS.toHours(delta)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(delta - TimeUnit.HOURS.toMillis(hours))
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(delta - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(
            minutes))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}