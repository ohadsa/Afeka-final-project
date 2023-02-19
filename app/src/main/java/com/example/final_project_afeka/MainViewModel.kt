package com.example.final_project_afeka

import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project_afeka.data.Hazard
import com.example.final_project_afeka.data.MyTime
import com.example.final_project_afeka.data.PermissionData
import com.example.final_project_afeka.data.durationFromTime
import com.example.final_project_afeka.location.Loc
import com.example.final_project_afeka.utils.SharedPreferenceUtil
import com.example.final_project_afeka.utils.permissions.PermissionRequestHandlerImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences,
    val permissionManager: PermissionRequestHandlerImpl,
) : ViewModel() {


    val hazardAround = MutableStateFlow(listOf<Hazard>())
    val location = MutableStateFlow<Loc?>(null)

    val permissionRequesterFlow =
        MutableSharedFlow<PermissionData>(replay = 1, extraBufferCapacity = 1)

    private val _drivingCounter = MutableStateFlow<Long?>(null)
    val startTime = MutableStateFlow<Long?>(null)
    val finisDriving = MutableStateFlow(false)
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
                finisDriving.value = false
                _drivingCounter.value = it
            }
        }
        viewModelScope.launch {
            endTime.collect {
                _drivingCounter.value = null
                startTime.value = null
                finisDriving.value = it != null
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
        finisDriving.value = true

    }

    fun logoutTapped() {
        auth.signOut()
    }

    fun hazardTriggered() {
    }

    fun notifyLocationChanged(newVal: Loc) {
        location.value = newVal
    }
}

