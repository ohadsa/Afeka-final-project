package com.example.final_project_afeka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


class MapFragment :  Fragment(R.layout.fragment_map){

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ComposeView>(R.id.composeViewMap).setContent {
            val location by viewModel.location.collectAsState()
            val hazards by viewModel.hazardAround.collectAsState()
            val cur = location?.let { LatLng(it.lat, it.lon) } ?: LatLng(32.18 , 34.81)//home
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(cur, 15f)
            }

            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(0.84f)
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                        .clip(
                            RoundedCornerShape(12.dp)),
                    cameraPositionState = cameraPositionState,
                ) {
                    Marker(
                        state = MarkerState(position =  LatLng(32.18 , 34.81)),
                        title = "Hazard",
                        snippet = "Hazard"
                    )
                }
            }
        }

    }
}