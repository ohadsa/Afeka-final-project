package com.example.final_project_afeka.services

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.final_project_afeka.HAZARDS_TAG_FB
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.data.Hazard
import com.example.final_project_afeka.fragments.SaveHazardPopup
import com.example.final_project_afeka.ui.theme.generic.MyText
import com.example.final_project_afeka.utils.permissions.PermissionRequestHandlerImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject


@AndroidEntryPoint
class BumpDialogActivity : AppCompatActivity() {

    private val viewModel: BumpDialogViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        if(latitude == 0.0 && longitude == 0.0) finish()

        setContent {
            var curLevel by remember {
                mutableStateOf(HazardLevel.LOW)
            }
            AlertDialog(
                modifier = Modifier
                    .width(300.dp),
                onDismissRequest = { finish() },
                buttons = {
                    SaveHazardPopup(
                        curLevel = curLevel,
                        onLevelChanged ={
                            curLevel = it
                        },
                        onDismiss = { finish() },
                        onSave = {
                            viewModel.saveNewHazard(
                                HazardResponse(
                                    latitude,
                                    longitude,
                                    curLevel
                                )
                            )
                            finish()
                        }
                    )
                }
            )
        }
    }
}

@HiltViewModel
class BumpDialogViewModel @Inject constructor(
    private val realTimeDB: FirebaseDatabase,
) : ViewModel() {

    fun saveNewHazard(data: HazardResponse) {
        realTimeDB.getReference(HAZARDS_TAG_FB).child(generateKey(data.lon, data.lat))
            .setValue(data)
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

}

data class HazardResponse(
    val lat: Double,
    val lon: Double,
    val level: HazardLevel
    )

enum class HazardLevel {
    LOW, MEDIUM, HIGH
}