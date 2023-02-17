package com.example.final_project_afeka.login.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project_afeka.ui.generic.ButtonV2
import com.example.final_project_afeka.ui.generic.ButtonVariant
import com.example.final_project_afeka.ui.generic.FontName
import com.example.final_project_afeka.ui.generic.MyFont
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText

@Composable
fun EmptyStateComponent(
    modifier: Modifier = Modifier,
    @DrawableRes drawable: Int?,
    title: String,
    description: String,
    buttonText: String = "",
    onButton: (() -> Unit)? = null,
) {
    if (drawable == null) return
    Box(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DrawableImage(
                id = drawable,
                modifier = Modifier
                    .height(74.dp)
                    .width(128.dp),
                svg = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyText(
                text = title,
                modifier = Modifier,
                font = MyFont(FontWeight.SemiBold, 16.sp, FontName.Poppins),
                color = MyColors.darkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            MyText(
                text = description,
                modifier = Modifier,
                font = MyFont(FontWeight.Normal, 14.sp, FontName.DMSans),
                color = MyColors.gray50
            )
            Spacer(modifier = Modifier.height(24.dp))
            onButton?.let {
                ButtonV2(
                    modifier = Modifier
                        .width(196.dp)
                        .clip(RoundedCornerShape(40.dp)),
                    onClick = onButton,
                    variant = ButtonVariant.Primary,
                    text = buttonText
                )
            }
        }
    }
}
