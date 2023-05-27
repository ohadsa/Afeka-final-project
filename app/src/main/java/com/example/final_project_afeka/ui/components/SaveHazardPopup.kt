package com.example.final_project_afeka.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.final_project_afeka.R
import com.example.final_project_afeka.services.HazardLevel
import com.example.final_project_afeka.ui.generic.*
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText

@Composable
fun SaveHazardPopup(
    curLevel: HazardLevel,
    onLevelChanged : (HazardLevel) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    Box {
        Column(
            Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .align(Alignment.Center)) {
            ClickableTopBar(
                leftId = R.drawable.left_arrow,
                onLeft = { onDismiss() },
                left = null,
                middle = stringResource(id = R.string.report)
            )
            CostumeDivider(modifier = Modifier.padding(top = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DrawableImage(id = R.drawable.location_save,
                    svg = false,
                    modifier = Modifier.size(60.dp))
            }
            Column(Modifier.padding(horizontal = 12.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                MyText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.report_title),
                    font = MyFont.Heading6)
                Spacer(modifier = Modifier.height(4.dp))
                MyText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.report_text),
                    font = MyFont.Body16,
                    color = MyColors.gray50)
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HazardLevel.values().forEach {
                        Column {
                        RadioButton(
                            selected = curLevel == it,
                            onClick = { onLevelChanged(it) }
                        )
                        MyText(
                            modifier = Modifier
                                .padding(bottom = 4.dp),
                            textAlign = TextAlign.Center,
                            text = it.name,
                            font = MyFont.ButtonSmall,
                            color = MyColors.darkGray)
                        }
                    }
                }
                CostumeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(4.dp),
                    borderSize = 1.dp,
                    text = stringResource(id = R.string.save),
                ) {
                    onSave()
                }
                Spacer(modifier = Modifier.height(12.dp))
                CostumeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(4.dp),
                    text = stringResource(id = R.string.cancel),
                    textColor = MyColors.danger,
                    borderColor = MyColors.danger,
                    borderSize = 1.dp,
                    buttonColor = MyColors.danger25
                ) {
                    onDismiss()
                }
                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
}