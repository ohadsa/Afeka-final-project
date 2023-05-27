package com.example.final_project_afeka.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.final_project_afeka.ui.generic.*
import com.example.final_project_afeka.ui.theme.generic.MyText

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