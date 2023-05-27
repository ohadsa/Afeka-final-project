package com.example.final_project_afeka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.services.objects.LocationData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.final_project_afeka.ui.pages.MapPage

val defaultLoc = LocationData(0.0 , 0.0, 0f)
class MapFragment :  Fragment(R.layout.fragment_map){

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ComposeView>(R.id.composeViewMap).setContent {
            val location by viewModel.location.collectAsState()
            val hazards by viewModel.hazardAround.collectAsState()
            val cur by viewModel.lastLocation.collectAsState()
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(cur?.latitude ?: defaultLoc.latitude,cur?.longitude?: defaultLoc.longitude ), 15f)
            }
            MapPage(
                cameraPositionState = cameraPositionState,
                hazards = hazards,
            ){
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

    }

}
