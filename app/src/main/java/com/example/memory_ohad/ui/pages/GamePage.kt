package com.example.memory_ohad.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.memory_ohad.ui.generic.MyFont
import com.example.memory_ohad.ui.theme.MyColors
import com.example.memory_ohad.ui.theme.generic.MyText

@Composable
fun GamePage(
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        MyText(
            text = "that page will be game page ",
            font = MyFont.Heading5,
            color = MyColors.main35,
            lineHeight = MyFont.Heading5.lineHeight)
    }
}