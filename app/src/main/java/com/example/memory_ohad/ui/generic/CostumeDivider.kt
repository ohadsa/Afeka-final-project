package com.example.memory_ohad.ui.generic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.memory_ohad.ui.theme.MyColors

@Composable
fun CostumeDivider(
    modifier: Modifier = Modifier,
    height: Dp = 1.dp,
    color: Color = MyColors.gray5
) {
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .background(color)
        )
    }
}