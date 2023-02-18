package com.example.final_project_afeka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.fragment.app.activityViewModels
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.ui.generic.*
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText


class DriveFragment : Fragment(R.layout.fragment_drive) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ComposeView>(R.id.composeViewDrive).setContent {
            DrivePage(viewModel) {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }

        }

    }
}

@Composable
fun DrivePage(
    viewModel: MainViewModel,
    onBack: () -> Unit,
) {
    val drivingCounter by viewModel.drivingCounter.collectAsState(null)
    val isDriving = drivingCounter != null
    val endTime by viewModel.endTime.collectAsState()

    Box(Modifier.fillMaxSize()) {
        DrawableImage(id = R.drawable.top_left_circles,
            svg = false,
            modifier = Modifier
                .width(200.dp)
                .height(144.dp)
                .align(
                    Alignment.TopStart))
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ClickableTopBar(
                    left = null,
                    right = null,
                    leftId = R.drawable.left_arrow,
                    onLeft = {
                        onBack()
                    }
                )
                Spacer(modifier = Modifier.height(160.dp))
                CircularProgressbar(
                    number = drivingCounter?.seconds?.toFloat()
                        ?: 0f.also { println("number = $it") },
                    indicatorThickness = 20.dp,
                    size = 250.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        MyText(
                            modifier = Modifier
                                .padding(top = 3.dp),
                            text = drivingCounter?.formatted ?: "",
                            font = MyFont(weight = FontWeight.W800,
                                textSize = 40.sp,
                                fontName = FontName.Poppins),
                            color = MyColors.darkGray,
                        )
                        MyText(
                            modifier = Modifier,
                            text = stringResource(id = R.string.duration),
                            font = MyFont(weight = FontWeight.W600,
                                textSize = 12.sp,
                                fontName = FontName.Poppins),
                            color = MyColors.gray50,
                        )
                    }
                }
            }
            Column {
                CostumeButton(
                    text =stringResource(id = R.string.report),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    viewModel.hazardTriggered()
                }
                Spacer(modifier = Modifier.height(8.dp))
                CostumeButton(
                    text =  stringResource(id = R.string.stop_driving),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    onBack()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }
    }
}

@Composable
fun CostumeButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    color: Color = Color.White,
    font: MyFont =  MyFont.Heading6,
    text: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(modifier
        .graphicsLayer {
            scaleX = if (isPressed) 0.98f else 1.0f
            scaleY = if (isPressed) 0.98f else 1.0f
        }
        .border(5.dp, Color("#4AB5A4".toColorInt()), shape = shape)
        .background(Color("#67CEBF".toColorInt()), shape)
        .clip(shape)
        .clickableNoFeedback(interactionSource) {
            onClick()
        }

    ) {
        MyText(text = text,
            font = font,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            color = color,
            textAlign = TextAlign.Center)
    }
}