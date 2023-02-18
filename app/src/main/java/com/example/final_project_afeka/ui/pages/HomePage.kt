package com.example.final_project_afeka.ui.pages

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.fragments.CostumeButton
import com.example.final_project_afeka.ui.generic.ClickableTopBar
import com.example.final_project_afeka.ui.generic.FontName
import com.example.final_project_afeka.ui.generic.MyFont
import com.example.final_project_afeka.ui.generic.clickableNoFeedback
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText

@Composable
fun HomePage(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    goToMap: () -> Unit,
    startDriving: () -> Unit,
) {


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
                onLeft = {
                    viewModel.logoutTapped()
                    onBack()
                }
            )
            Spacer(modifier = Modifier.height(100.dp))
            CostumeButton(
                modifier = Modifier.size(200.dp),
                font = MyFont(weight = FontWeight.W800,
                    textSize = 24.sp,
                    fontName = FontName.DMSans),
                text = stringResource(id = R.string.start)) {
                viewModel.startDriving()
                startDriving()
            }

            Spacer(modifier = Modifier.height(40.dp))
            MyText(text = stringResource(id = R.string.see_hazard),
                font = MyFont.Heading6)
            Spacer(modifier = Modifier.height(24.dp))
            MapButton() {
                goToMap()
            }

            /*
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

             */
        }
    }
}

@Composable
fun MapButton(
    onClick: () -> Unit,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    DrawableImage(id = R.drawable.map_, svg = false,
        Modifier
            .graphicsLayer {
                scaleX = if (isPressed) 0.98f else 1.0f
                scaleY = if (isPressed) 0.98f else 1.0f
            }
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(254.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickableNoFeedback(interactionSource) {
                onClick()
            }

    )
}

