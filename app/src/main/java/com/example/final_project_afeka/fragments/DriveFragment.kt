package com.example.final_project_afeka.fragments

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
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
                viewModel.stopDriving()
                activity?.onBackPressed()
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
    val location by viewModel.location.collectAsState()
    val openHazardDialog by viewModel.openHazardDialog.collectAsState()
    val pinedLocation by viewModel.pinnedLocation.collectAsState()



    Box(Modifier.fillMaxSize()) {
        DrawableImage(id = R.drawable.top_left_circles,
            svg = false,
            modifier = Modifier
                .width(200.dp)
                .height(144.dp)
                .align(
                    Alignment.TopStart))

        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            ClickableTopBar(
                left = null,
                right = null,
                leftId = R.drawable.left_arrow,
                onLeft = {
                    onBack()
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                CircularProgressbar(

                    number = drivingCounter?.seconds?.toFloat()
                        ?: 0f,
                    indicatorThickness = 12.dp,
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
                            modifier = Modifier
                                .padding(top = 3.dp),
                            text = "Driving",
                            font = MyFont(weight = FontWeight.W800,
                                textSize = 16.sp,
                                fontName = FontName.Poppins),
                            color = MyColors.darkGray,
                        )

                    }
                }
            }

            Column {
                CostumeButton(
                    text = stringResource(id = R.string.report),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    viewModel.hazardTriggered(location)
                }
                Spacer(modifier = Modifier.height(8.dp))
                CostumeButton(
                    text = stringResource(id = R.string.stop_driving),
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

        if (openHazardDialog)
            Dialog(onDismissRequest = { viewModel.closeHazardDialog() }) {
                SaveHazardPopup(
                    onDismiss = { viewModel.closeHazardDialog() },
                    onSave = {
                        pinedLocation?.let {
                            viewModel.saveNewHazard(it)
                            viewModel.closeHazardDialog()
                        }
                    }
                )
            }
    }
}

@Composable
fun SaveHazardPopup(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .align(Alignment.Center)) {
            ClickableTopBar(
                leftId = R.drawable.left_arrow,
                onLeft = { onDismiss() },
                left = null,
                middle = stringResource(id = R.string.report)
            )
            CostumeDivider(modifier = Modifier.padding(top = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DrawableImage(id = R.drawable.location_save,
                    svg = false,
                    modifier = Modifier.size(60.dp))
            }
            Column(Modifier.padding(horizontal = 12.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                MyText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.report_title),
                    font = MyFont.Heading6)
                Spacer(modifier = Modifier.height(4.dp))
                MyText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.report_text),
                    font = MyFont.Body16,
                    color = MyColors.gray50)
                Spacer(modifier = Modifier.height(24.dp))
                CostumeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(4.dp),
                    borderSize = 1.dp,
                    text = stringResource(id = R.string.save),
                ) {
                    onSave()
                }
                Spacer(modifier = Modifier.height(12.dp))
                CostumeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(4.dp),
                    text = stringResource(id = R.string.cancel),
                    textColor = MyColors.danger,
                    borderColor = MyColors.danger,
                    borderSize = 1.dp,
                    buttonColor = MyColors.danger25
                ) {
                    onDismiss()
                }
                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
}


@Composable
fun CostumeButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    textColor: Color = Color.White,
    buttonColor: Color = Color("#67CEBF".toColorInt()),
    borderColor: Color = Color("#4AB5A4".toColorInt()),
    borderSize: Dp = 5.dp,
    font: MyFont = MyFont.Heading6,
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
        .border(borderSize, borderColor, shape = shape)
        .background(buttonColor, shape)
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
            color = textColor,
            textAlign = TextAlign.Center)
    }
}