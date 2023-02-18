package com.example.final_project_afeka

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project_afeka.login.data.MyTime
import com.example.final_project_afeka.login.data.durationFromTime
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {
    private val _drivingCounter = MutableStateFlow<Long?>(null)
    private val startTime = MutableStateFlow<Long?>(null)
    val endTime = MutableStateFlow<MyTime?>(null)
    val drivingCounter = _drivingCounter.combine(startTime) { counter, start ->
        if (counter == null || start == null) null
        else start.durationFromTime()
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
                    _drivingCounter.value = System.currentTimeMillis()
                }
            }
        }
    }

    fun stopDriving() {
        val start = startTime.value ?: 0
        endTime.value = MyTime(start, start.durationFromTime())
    }

    fun logoutTapped() {
        auth.signOut()
    }

    fun hazardTriggered() {
    }

}

