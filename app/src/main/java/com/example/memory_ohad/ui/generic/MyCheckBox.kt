package com.example.memory_ohad.ui.generic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.memory_ohad.R
import com.example.memory_ohad.ui.theme.MyColors
import com.example.memory_ohad.ui.theme.generic.DrawableImage

@Composable
fun MyCheckBox(isChecked: Boolean) {
    Box(
        modifier = Modifier
            .height(16.dp)
            .width(16.dp)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = if (!isChecked) MyColors.gray15
                    else Color.Transparent
                ),
                shape = RoundedCornerShape(2.dp)
            )
    ) {
        if (isChecked) {
            DrawableImage(
                modifier = Modifier
                    .background(MyColors.indigoPrimary, RoundedCornerShape(2.dp))
                    .height(16.dp)
                    .width(16.dp),
                id = R.drawable.approve, svg = true
            )
        }
    }
}
