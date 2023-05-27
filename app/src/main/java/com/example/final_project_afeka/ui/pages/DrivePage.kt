package com.example.final_project_afeka.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.ui.components.CostumeButton
import com.example.final_project_afeka.ui.generic.CircularProgressbar
import com.example.final_project_afeka.ui.generic.ClickableTopBar
import com.example.final_project_afeka.ui.generic.FontName
import com.example.final_project_afeka.ui.generic.MyFont
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText

@Composable
fun DrivePage(
    viewModel: MainViewModel,
    onBack: () -> Unit,
) {
    val drivingCounter by viewModel.drivingCounter.collectAsState(null)
    val location by viewModel.location.collectAsState()



    Box(Modifier.fillMaxSize()) {
        DrawableImage(id = R.drawable.top_left_circles,
            svg = false,
            modifier = Modifier
                .width(200.dp)
                .height(144.dp)
                .align(
                    Alignment.TopStart
                ))

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
    }
}