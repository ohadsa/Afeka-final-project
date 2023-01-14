package com.example.memory_ohad.ui.theme.generic
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memory_ohad.R
import com.example.memory_ohad.ui.generic.MyFont
import com.example.memory_ohad.ui.generic.clickableNoFeedback
import com.example.memory_ohad.ui.theme.MyColors

@OptIn(ExperimentalComposeUiApi::class)
@Composable

fun SearchBar(
    text : String = "",
    placeholder: @Composable (() -> Unit)? = null,
    focusChanged: (Boolean) -> Unit,
    searchClicked: (String) -> Unit,
    onTextChange: (String) -> Unit,
    rightButton: @Composable (() -> Unit)? = null,
    onCancel: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .padding(vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(8.dp))
            OutlineTextField(
                value = text,
                onValueChange = {
                    onTextChange(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged {
                        focusChanged(it.isFocused)
                        isFocused = it.isFocused
                    }
                    .heightIn(min = 40.dp),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    searchClicked(text)
                }),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                contentPadding = PaddingValues(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MyColors.gray35,
                    unfocusedBorderColor = MyColors.gray10,
                    backgroundColor = MyColors.gray5
                ),
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DrawableImage(
                            id = R.drawable.search,
                            svg = true,
                            modifier = Modifier
                                .size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        if (placeholder != null)
                            placeholder()
                        else
                            MyText(
                                text = stringResource(id = R.string.search),
                                font = MyFont.Body14,
                                color = MyColors.gray50,
                                maxLines = 1
                            )
                    }
                },
                shape = RoundedCornerShape(percent = 50)
            )
            if (isFocused) {
                MyText(
                    text = stringResource(id = R.string.cancel),
                    font = MyFont.ButtonMedium,
                    color = MyColors.indigoPrimary,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickableNoFeedback {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onCancel()
                        },
                )
            } else {
                rightButton?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                    it()
                }
            }
        }
    }


}

@Preview
@Composable
fun PreviewSearchBar() {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
    ) {
        SearchBar(
            focusChanged = {},
            rightButton = { Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Blue))
            }, searchClicked = {} , onTextChange = {} , onCancel = {})
    }
}