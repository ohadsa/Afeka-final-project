package com.example.final_project_afeka.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.final_project_afeka.R
import com.example.final_project_afeka.data.Hazard
import com.example.final_project_afeka.ui.generic.ClickableTopBar
import com.example.final_project_afeka.ui.generic.CostumeDivider
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapPage(
    cameraPositionState: CameraPositionState,
    hazards : List<Hazard>,
    onBack : ()-> Unit,
    ) {
    Box(Modifier.fillMaxSize()) {
        Column {
            ClickableTopBar(
                left = null,
                leftId = R.drawable.left_arrow,
                middle = stringResource(id = R.string.map_fragment_title),
                onLeft = {
                    onBack()
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