package com.example.final_project_afeka.ui.generic

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText

const val emptyString = ""
@Composable
fun ClickableTopBar(
    modifier: Modifier = Modifier,
    enabledRight: Boolean = true,
    enabledLeft: Boolean = true,
    right: String? = emptyString,
    @DrawableRes rightId: Int? = null,
    onRight: () -> Unit = {},
    middle: String? = emptyString,
    middleColor: Color = MyColors.darkGray,
    left: String? = emptyString,
    onLeft: () -> Unit = {},
    @DrawableRes leftId: Int? = null,

    ) {

    val interactionSourceRight = remember { MutableInteractionSource() }
    val interactionSourceLeft = remember { MutableInteractionSource() }
    val isPressedRight by interactionSourceRight.collectIsPressedAsState()
    val isPressedLeft by interactionSourceLeft.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,

        ) {

        middle?.let {
            MyText(
                modifier = Modifier.padding(top = 16.dp).align(Alignment.Center),
                text = it,
                lineHeight = MyFont.Heading5.lineHeight,
                color = middleColor,
                font = MyFont.Heading5,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            left?.let {
                MyText(
                    text = it,
                    lineHeight = MyFont.ButtonMedium.lineHeight,
                    color = if (!enabledLeft || isPressedLeft) MyColors.gray35 else MyColors.indigoPrimary,
                    font = MyFont.ButtonMedium,
                    modifier = Modifier.clickableNoFeedback(interactionSourceLeft) {
                        if (enabledLeft)
                            onLeft()
                    },

                    )

            }
            leftId?.let {
                DrawableImage(
                    id = it,
                    svg = true,
                    modifier = Modifier
                        .size(24.dp)
                        .clickableNoFeedback {
                            onLeft()
                        })
            }


            right?.let {
                MyText(
                    text = it,
                    lineHeight = MyFont.ButtonMedium.lineHeight,
                    color = if (!enabledRight || isPressedRight) MyColors.gray35 else MyColors.indigoPrimary,
                    font = MyFont.ButtonMedium,
                    modifier = Modifier.clickableNoFeedback(interactionSourceRight) {
                        if (enabledRight)
                            onRight()
                    },
                )
            }
            rightId?.let {
                DrawableImage(
                    modifier = Modifier
                        .clickableNoFeedback {
                            if (enabledRight) {
                                onRight()
                            }
                        }
                        .size(24.dp),
                    id = it, svg = true
                )
            }
        }
    }
}
