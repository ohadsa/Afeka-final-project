package com.example.final_project_afeka.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.formatToTime
import com.example.final_project_afeka.ui.generic.ButtonV2
import com.example.final_project_afeka.ui.generic.ClickableTopBar
import com.example.final_project_afeka.ui.generic.MyFont
import com.example.final_project_afeka.ui.generic.clickableNoFeedback
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText
import java.util.concurrent.TimeUnit

@Composable
fun HomePage(
    viewModel: MainViewModel,
) {
    val endTime by viewModel.endTime.collectAsState()
        println("endTime  $endTime")
    val drivingCounter by viewModel.drivingCounter.collectAsState(null)

    Box(Modifier.fillMaxSize()) {
        DrawableImage(id = R.drawable.top_left_circles,
            svg = false,
            modifier = Modifier
                .width(200.dp)
                .height(144.dp)
                .align(
                    Alignment.TopStart))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClickableTopBar(
                left = null,
                right = null,
                leftId = R.drawable.left_arrow,
            )
            Spacer(modifier = Modifier.height(60.dp))
            Box(Modifier
                .size(200.dp)
                .clip(CircleShape)
                .clickable {
                    if (drivingCounter != null)
                        viewModel.stopDriving()
                    else
                        viewModel.startDriving()

                }
                .background(Color("#4AB5A4".toColorInt()))
            ) {
                MyText(text = if (drivingCounter != null) "Stop Driving" else "Start driving",
                    font = MyFont.Heading6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center)
            }
            drivingCounter?.let {
                Spacer(modifier = Modifier.height(24.dp))
                MyText(text = "time : $drivingCounter",
                    font = MyFont.Heading6)
            }
            endTime?.let {
                Spacer(modifier = Modifier.height(24.dp))
                MyText(text = "total : $it",
                    font = MyFont.Heading6)
            }
        }
    }
}


