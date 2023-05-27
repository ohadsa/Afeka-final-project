package com.example.final_project_afeka

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project_afeka.data.Hazard
import com.example.final_project_afeka.data.MyTime
import com.example.final_project_afeka.data.PermissionData
import com.example.final_project_afeka.data.durationFromTime
import com.example.final_project_afeka.services.Loc
import com.example.final_project_afeka.services.LocationData
import com.example.final_project_afeka.utils.SharedPreferenceUtil.START_TIME_TAG
import com.example.final_project_afeka.utils.permissions.PermissionRequestHandlerImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val realTimeDB: FirebaseDatabase,
    private val sharedPreferences: SharedPreferences,
    val permissionManager: PermissionRequestHandlerImpl,
) : ViewModel() {


    val hazardAround = MutableStateFlow(listOf<Hazard>())
    val lastLocation = MutableStateFlow<LocationData?>(null)
    val location = MutableStateFlow<Loc?>(null)
    var initialStartTime: Long = sharedPreferences.getLong(START_TIME_TAG, -1)
    val permissionRequesterFlow =
        MutableSharedFlow<PermissionData>(replay = 1, extraBufferCapacity = 1)
    private val endTime = MutableStateFlow<MyTime?>(null)
    private val _drivingCounter = MutableStateFlow<Long?>(null)
    val startTime = MutableStateFlow<Long?>(null)
    val finishService = MutableStateFlow(false)
    val allowService =  MutableStateFlow(false)
    val openHazardDialog = MutableStateFlow(false)
    val pinnedLocation = MutableStateFlow<Loc?>(null)
    val drivingCounter = _drivingCounter.combine(startTime) { counter, start ->
        if (counter == null || start == null) null
        else start.durationFromTime()
    }
    var backToHome = false

    fun startDriving(time: Long = System.currentTimeMillis()) {
        startTime.value = time
        sharedPreferences.edit().putLong(START_TIME_TAG, time).apply()
    }

    init {
        initialTimerLogic()
        initHazardsAround()
    }

    private fun initHazardsAround() {
        realTimeDB.getReference(HAZARDS_TAG_FB).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hazards = mutableListOf<Hazard>()
                for (shot in snapshot.children) {
                    val loc = shot.getValue(Loc::class.java)
                    loc?.let {
                        hazards.add(Hazard(it, "Hazard", ""))
                    }
                }
                hazardAround.value = hazards
            }

            override fun onCancelled(error: DatabaseError) = Unit

        })
    }

    private fun initialTimerLogic() {
        viewModelScope.launch {
            startTime.collect {
                finishService.value = false
                _drivingCounter.value = it
            }
        }
        viewModelScope.launch {
            endTime.collect {
                _drivingCounter.value = null
                startTime.value = null
                finishService.value = it != null
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
    fun stopService(){
        finishService.value = true
    }

    fun stopDriving() {
        val start = startTime.value ?: 0
        endTime.value = MyTime(start, start.durationFromTime())
        finishService.value = true
        sharedPreferences.edit().remove(START_TIME_TAG).apply()
        initialStartTime = -1

    }

    fun logoutTapped() {
        auth.signOut()
    }

    fun hazardTriggered(loc: Loc?) {
        pinnedLocation.value = loc
        openHazardDialog.value = true
    }


    fun closeHazardDialog() {
        openHazardDialog.value = false
    }

    fun saveNewHazard(location: Loc) {
        realTimeDB.getReference(HAZARDS_TAG_FB).child(generateKey(location.lon, location.lat))
            .setValue(location)
    }

    private fun generateKey(lon: Double, lat: Double): String {
        val buffer = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN)
        buffer.putDouble(lon)
        buffer.putDouble(lat)
        val bytes = buffer.array()
        return bytes.joinToString("") { String.format("%02X", it) }
    }

    private fun reverseKey(key: String): Pair<Double, Double> {
        val bytes = key.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val a = buffer.double
        val b = buffer.double
        return Pair(a, b)
    }

    fun startService() {
        allowService.value = true
    }
}

const val HAZARDS_TAG_FB = "hazards"
