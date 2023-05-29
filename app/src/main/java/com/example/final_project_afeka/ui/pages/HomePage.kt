package com.example.final_project_afeka.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.RadioButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.login.ui.composable.ChooseAvatarPage
import com.example.final_project_afeka.services.objects.Sensitivity
import com.example.final_project_afeka.ui.components.CostumeButton
import com.example.final_project_afeka.ui.generic.AnimatedDialog
import com.example.final_project_afeka.ui.generic.ClickableTopBar
import com.example.final_project_afeka.ui.generic.CostumeDivider
import com.example.final_project_afeka.ui.generic.FontName
import com.example.final_project_afeka.ui.generic.MyFont
import com.example.final_project_afeka.ui.generic.clickableNoFeedback
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText
import com.example.final_project_afeka.utils.SharedPreferenceUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomePage(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    goToMap: () -> Unit,
    startDriving: () -> Unit,
) {
    val selectedSensitivity = remember { mutableStateOf(SharedPreferenceUtil.readSensitivity()) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    fun showSheet(show: Boolean) =
        scope.launch {
            if (show) sheetState.show()
            else sheetState.hide()
        }
    ModalBottomSheetLayout(
        sheetShape = MaterialTheme.shapes.large.copy(
            topStart = CornerSize(12.dp), topEnd = CornerSize(12.dp)
        ),
        sheetState = sheetState,
        sheetContent = {
            SensitivityRadioGroup(selectedSensitivity) { sensitivity ->
                selectedSensitivity.value = sensitivity
                SharedPreferenceUtil.writeSensitivity(sensitivity)
                showSheet(false)
            }
        },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())) {
            DrawableImage(id = R.drawable.top_left_circles,
                svg = false,
                modifier = Modifier
                    .width(200.dp)
                    .height(144.dp)
                    .align(
                        Alignment.TopStart
                    ))

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
                    startDriving()
                }

                Spacer(modifier = Modifier.height(40.dp))
                MyText(text = stringResource(id = R.string.see_hazard),
                    font = MyFont.Heading6)
                Spacer(modifier = Modifier.height(24.dp))
                MapButton {
                    goToMap()
                }
                Spacer(modifier = Modifier.height(24.dp))
                CostumeButton(
                    text = "Change Sensor Sensitivity",
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    showSheet(true)
                }
            }
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

@Composable
fun SensitivityRadioGroup(selectedSensitivity: MutableState<Sensitivity>, onSensitivitySelected: (Sensitivity) -> Unit) {
    Column(Modifier.padding(start = 12.dp , end= 12.dp , bottom = 12.dp )) {
        ClickableTopBar(middle = "Sensitivity")
        CostumeDivider(modifier = Modifier.padding(vertical = 12.dp))
        Sensitivity.values().forEach { sensitivity ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickableNoFeedback { onSensitivitySelected(sensitivity) },
                verticalAlignment = CenterVertically
            ) {
                RadioButton(
                    selected = (selectedSensitivity.value == sensitivity),
                    onClick = { onSensitivitySelected(sensitivity) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                MyText(text = sensitivity.text, font = MyFont.Heading5)
            }
        }
    }
}
