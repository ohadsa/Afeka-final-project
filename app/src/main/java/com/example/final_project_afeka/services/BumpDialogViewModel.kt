package com.example.final_project_afeka.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project_afeka.HAZARDS_TAG_FB
import com.example.final_project_afeka.data.Hazard
import com.example.final_project_afeka.services.objects.Loc
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

@HiltViewModel
class BumpDialogViewModel @Inject constructor(
    private val realTimeDB: FirebaseDatabase,
) : ViewModel() {

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


    private fun combinedLocation(
        closeHazards: List<HazardResponse>,
        newHazard: HazardResponse
    ): HazardResponse {
        var totalLat = 0.0
        var totalLon = 0.0
        var totalReports = 0
        var totalLevel = 0

        for (hazard in closeHazards) {
            totalLat += hazard.lat
            totalLon += hazard.lon
            totalReports += hazard.reports
            totalLevel += hazard.level.toInt().times(hazard.reports)
        }

        totalLat += newHazard.lat
        totalLon += newHazard.lon
        totalReports += newHazard.reports
        totalLevel += newHazard.level.toInt()

        val avgLat = totalLat / (closeHazards.size + 1)
        val avgLon = totalLon / (closeHazards.size + 1)
        println("total $totalLevel")
        println("totalReports $totalReports")
        println("total / size  ${totalLevel.toDouble().div((totalReports + 1).toDouble())}")
        val avgLevel = (totalLevel.toDouble().div((totalReports + 1).toDouble()))
            .toHazardLevel()

        return HazardResponse(avgLat, avgLon, avgLevel, totalReports)
    }


    private fun calculateDistanceInMeter(loc1: Loc, loc2: Loc): Double {
        val earthRadius = 6371 // radius in kilometers

        val dLat = Math.toRadians(loc2.lat - loc1.lat)
        val dLon = Math.toRadians(loc2.lon - loc1.lon)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(loc1.lat)) * Math.cos(Math.toRadians(loc2.lat)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val distance = earthRadius * c

        return distance * 1000 // convert to meters
    }

    private suspend fun getAllHazardsIn50MetersRadius(newHazard: HazardResponse): List<HazardResponse> {
        val allHazards: List<HazardResponse> = initHazardsAround()
        val closeHazards: MutableList<HazardResponse> = ArrayList()

        for (hazard in allHazards) {
            val distance = calculateDistanceInMeter(
                Loc(hazard.lat, hazard.lon),
                Loc(newHazard.lat, newHazard.lon)
            )
            if (distance <= 50 ) {
                closeHazards.add(hazard)
            }
        }

        return closeHazards
    }

    suspend fun saveNewHazard(hazard: HazardResponse) {
            try {
                val closeHazards: List<HazardResponse> = getAllHazardsIn50MetersRadius(hazard)
                val hazardToSave: HazardResponse = combinedLocation(closeHazards, hazard)

                realTimeDB.getReference(HAZARDS_TAG_FB)
                    .child(generateKey(hazardToSave.lon, hazardToSave.lat))
                    .setValue(hazardToSave)

                // Remove the close hazards.
                for (oldHazard in closeHazards) {
                    realTimeDB.getReference(HAZARDS_TAG_FB)
                        .child(generateKey(oldHazard.lon, oldHazard.lat))
                        .removeValue()
                }
            } catch (e: Exception) {
                // Handle the exception, e.g., log the error or show a message to the user.
                println("saveNewHazard Error: ${e.message}")
            }
    }


    private suspend fun initHazardsAround(): List<HazardResponse> =
        suspendCancellableCoroutine { continuation ->
            realTimeDB.getReference(HAZARDS_TAG_FB)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val hazards = mutableListOf<HazardResponse>()
                        for (shot in snapshot.children) {
                            val hazardResponse = shot.getValue(HazardResponse::class.java)
                            hazardResponse?.let {
                                hazards.add(it)
                            }
                        }
                        if (continuation.isActive) {
                            continuation.resume(hazards)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception(error.toException()))
                        }
                    }
                })
        }


}
