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
import androidx.lifecycle.lifecycleScope
import com.example.final_project_afeka.HAZARDS_TAG_FB
import com.example.final_project_afeka.ui.components.SaveHazardPopup
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
                            lifecycleScope.launch {
                                viewModel.saveNewHazard(
                                    HazardResponse(
                                        latitude,
                                        longitude,
                                        curLevel
                                    )
                                )
                                finish()
                            }
                        }
                    )
                }
            )
        }
    }
}


data class HazardResponse(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val level: HazardLevel = HazardLevel.LOW,
    val reports: Int = 1
    )

enum class HazardLevel {
    LOW, MEDIUM, HIGH
}

fun HazardLevel.toInt(): Int {
    return when(this) {
        HazardLevel.LOW -> 0
        HazardLevel.MEDIUM -> 1
        HazardLevel.HIGH -> 2
    }
}

fun Double.toHazardLevel(): HazardLevel {
    return when {
        this < 0.5 -> HazardLevel.LOW
        this < 1.5  -> HazardLevel.MEDIUM
        else -> HazardLevel.HIGH
    }
}