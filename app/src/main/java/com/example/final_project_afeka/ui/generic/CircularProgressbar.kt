package com.example.final_project_afeka.ui.generic

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

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.final_project_afeka.ui.theme.MyColors

@Composable
fun CircularProgressbar(
    modifier: Modifier = Modifier,
    number: Float = 70f,
    size: Dp = 60.dp,
    indicatorThickness: Dp = 6.dp,
    animationDuration: Int = 0,
    animationDelay: Int = 0,
    foregroundIndicatorColor: Color = MyColors.main,
    backgroundIndicatorColor: Color = MyColors.main.copy(alpha = 0.1f),
    content: @Composable () -> Unit,
    ) {

    // Number Animation
    val animateNumber = animateFloatAsState(
        targetValue = number,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )


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

            val sweepAngle = (animateNumber.value / 60) * 360

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
            contentAlignment = Alignment.Center
        ) {
            content()
        }

    }

    Spacer(modifier = Modifier.height(32.dp))

}

