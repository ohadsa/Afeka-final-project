package com.example.final_project_afeka.ui.generic


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage

enum class ButtonVariant {
    Primary, Secondary, Tertiary
}

class ButtonIcon(
    @DrawableRes val icon: Int,
    val svg: Boolean
)

enum class ButtonSize {
    Small, Medium, Large
}

@Composable
fun ButtonV2(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    variant: ButtonVariant = ButtonVariant.Primary,
    leftIcon: ButtonIcon? = null,
    rightIcon: ButtonIcon? = null,
    cornerRadius: Dp = 4.dp,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    userInteractionEnabled: Boolean = true,
) {
    val backgroundColor = when (variant) {
        ButtonVariant.Primary -> if (enabled) MyColors.indigoPrimary else MyColors.gray10
        ButtonVariant.Secondary -> Color.White
        ButtonVariant.Tertiary -> Color.Transparent
    }
    val horizontalSpacing = when (size) {
        ButtonSize.Small -> 4.dp
        ButtonSize.Medium -> 8.dp
        ButtonSize.Large -> 16.dp
    }
    Box(
        modifier = modifier
            .let {
                if (variant == ButtonVariant.Secondary)
                    it.border(
                        1.dp,
                        if (enabled) MyColors.indigo15 else MyColors.gray15,
                        RoundedCornerShape(cornerRadius)
                    )
                else
                    it
            }
            .clip(RoundedCornerShape(cornerRadius))
            .let {
                if (enabled) {
                    val hoverColor = when (variant) {
                        ButtonVariant.Primary -> MyColors.indigoDark
                        ButtonVariant.Secondary -> MyColors.indigo25
                        ButtonVariant.Tertiary -> MyColors.indigo5
                    }
                    if (userInteractionEnabled)
                        it.clickable(
                            onClick = onClick,
                            indication = rememberRipple(
                                bounded = true,
                                color = hoverColor
                            ),
                            interactionSource = remember {
                                MutableInteractionSource()
                            })
                    else it
                } else it
            }
            .background(backgroundColor)
            .padding(all = horizontalSpacing)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val primaryColor = when (variant) {
                ButtonVariant.Primary -> if (enabled) Color.White else MyColors.gray35
                ButtonVariant.Secondary, ButtonVariant.Tertiary -> if (enabled) MyColors.indigoPrimary else MyColors.gray35
            }
            if (leftIcon != null) {
                if (LocalInspectionMode.current) {
                    Image(
                        painter = painterResource(id = leftIcon.icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(18.dp),
                        colorFilter = ColorFilter.tint(primaryColor)
                    )
                } else {
                    DrawableImage(
                        id = leftIcon.icon,
                        svg = leftIcon.svg,
                        modifier = Modifier
                            .size(18.dp),
                        colorFilter = ColorFilter.tint(primaryColor)
                    )
                }
            }
            if (leftIcon != null && text.isNotEmpty())
                Spacer(modifier = Modifier.width(8.dp))
            if (text.isNotEmpty())
                Text(
                    text = text,
                    color = primaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            if (rightIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                if (LocalInspectionMode.current) {
                    Image(
                        painter = painterResource(id = rightIcon.icon),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(primaryColor)
                    )
                } else {
                    DrawableImage(
                        id = rightIcon.icon,
                        svg = rightIcon.svg,
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(primaryColor)
                    )
                }
            }
        }
    }
}