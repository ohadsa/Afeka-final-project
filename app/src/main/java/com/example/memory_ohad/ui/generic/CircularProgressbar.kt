package com.example.memory_ohad.ui.generic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight.Companion.Bold

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memory_ohad.ui.theme.MyColors
import com.example.memory_ohad.ui.theme.generic.MyText

@Composable
fun CircularProgressbar(
    modifier: Modifier = Modifier,
    number: Float = 70f,
    font: MyFont = MyFont(Bold, 14.sp, FontName.Poppins),
    textColor: Color = MyColors.darkGray,
    size: Dp = 60.dp,
    indicatorThickness: Dp = 6.dp,
    animationDuration: Int = 0,
    animationDelay: Int = 0,
    foregroundIndicatorColor: Color = MyColors.main,
    backgroundIndicatorColor: Color = MyColors.main.copy(alpha = 0.1f)
) {

    // It remembers the number value
    var numberR by remember {
        mutableStateOf(0f)
    }

    // Number Animation
    val animateNumber = animateFloatAsState(
        targetValue = numberR,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )

    // This is to start the animation when the activity is opened
    LaunchedEffect(Unit) {
        numberR = number
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size = size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size = size)
        ) {

            // Background circle
            drawCircle(
                color = backgroundIndicatorColor,
                radius = size.toPx() / 2,
                style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round)
            )

            val sweepAngle = (animateNumber.value / 100) * 360

            // Foreground circle
            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
        }

        // Text that shows number inside the circle
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(3.dp)
                .fillMaxSize()
                .background(Color.White, CircleShape),
        ) {
            MyText(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .align(Alignment.Center),
                text = (animateNumber.value).toInt().toString().formatToPercent(),
                font = font,
                color = textColor,
            )
        }

    }

    Spacer(modifier = Modifier.height(32.dp))

}

private fun String.formatToPercent(): String = "$this%"
