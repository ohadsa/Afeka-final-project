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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.ui.generic.ClickableTopBar
import com.example.final_project_afeka.ui.generic.CostumeDivider
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
            println("all hazards = ${hazards.map { "$it\n" }}")

            Box(Modifier.fillMaxSize()) {
                Column {
                    ClickableTopBar(
                        left = null,
                        leftId = R.drawable.left_arrow,
                        middle = stringResource(id = R.string.map_fragment_title),
                        onLeft = {
                            viewModel.stopService()
                            activity?.onBackPressed()
                        }
                    )
                    CostumeDivider(Modifier.padding(top = 16.dp))
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding( 12.dp)
                            .clip(
                                RoundedCornerShape(12.dp)),
                        cameraPositionState = cameraPositionState,

                    ) {
                        hazards.forEach {
                            Marker(
                                state = MarkerState(position = LatLng(it.loc.lat, it.loc.lon)),
                                title = it.title,
                                snippet = it.snippet
                            )
                        }
                    }
                }
            }
        }

    }
}