package com.example.final_project_afeka.ui.generic

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.final_project_afeka.R
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.MyText

sealed class TextFieldState {
    class Error(val error: Int) : TextFieldState()
    object None : TextFieldState()
    class Success(val message: Int?) : TextFieldState()
    object Loading : TextFieldState()
}

fun String.toDots() = this.map { '*' }.toString()

@Composable
fun CustomTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLines: Int = Int.MAX_VALUE,
    state: TextFieldState = TextFieldState.None,
    isPassword: Boolean = false,
    font: MyFont = MyFont.Body14,
    onFocus: (() -> Unit)? = null,
) {

    CustomTextField(
        value = value,
        onValueChanged = onValueChanged,
        modifier = modifier,
        placeholder = placeholder,
        maxLines = maxLines,
        state = state,
        isPassword = isPassword,
        keyboardOptions = KeyboardOptions.Default,
        font = font,
        onFocus = onFocus
    )
}

@Composable
fun CustomTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLines: Int = Int.MAX_VALUE,
    state: TextFieldState = TextFieldState.None,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions,
    font: MyFont = MyFont.Body14,
    onFocus: (() -> Unit)? = null,
) {
    CustomTextField(
        value = value,
        onValueChanged = onValueChanged,
        modifier = modifier,
        placeholder = placeholder,
        maxLines = maxLines,
        state = state,
        isPassword = isPassword,
        keyboardOptions = keyboardOptions,
        font = font,
        onFocus = onFocus,
        enabled = true)
}

@Composable
fun CustomTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLines: Int = Int.MAX_VALUE,
    state: TextFieldState = TextFieldState.None,
    isPassword: Boolean = false,
    enabled: Boolean,
    sameColorWhenDisable: Boolean = false,
    height: Dp? = null,
    keyboardOptions: KeyboardOptions,
    font: MyFont = MyFont.Body14,
    onFocus: (() -> Unit)? = null,
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        val focusedColor =
            (if (state is TextFieldState.Success) MyColors.success else MyColors.indigoPrimary)
        OutlinedTextField(
            keyboardOptions = keyboardOptions,
            value = if (passwordVisible || !isPassword) value else value.toDots(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = focusedColor,
                focusedBorderColor = focusedColor,
                errorBorderColor = MyColors.danger,
                errorLabelColor = MyColors.danger,
                unfocusedBorderColor = if (state is TextFieldState.Success) MyColors.success else MyColors.gray10,
                disabledTextColor = if (sameColorWhenDisable) LocalContentColor.current.copy(
                    LocalContentAlpha.current) else LocalContentColor.current.copy(LocalContentAlpha.current)
                    .copy(ContentAlpha.disabled),
                disabledBorderColor = if (sameColorWhenDisable) if (state is TextFieldState.Success) MyColors.success else MyColors.gray10 else focusedColor.copy(
                    ContentAlpha.disabled),
                disabledLabelColor = if (sameColorWhenDisable) MaterialTheme.colors.onSurface.copy(
                    ContentAlpha.medium) else MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                    .copy(ContentAlpha.disabled)
            ),
            onValueChange = onValueChanged,
            enabled = enabled,
            visualTransformation = if (passwordVisible || !isPassword) VisualTransformation.None else PasswordVisualTransformation(),
            label = {
                MyText(
                    text = placeholder,
                    font = font,
                    color = when (state) {
                        is TextFieldState.Error -> MyColors.danger
                        is TextFieldState.Success -> MyColors.success
                        else -> Color.Unspecified
                    }
                )
            },
            modifier = Modifier
                .let { mod ->
                    height?.let {
                        mod.height(it)
                    } ?: mod
                }
                .fillMaxWidth()
                .onFocusChanged { if (it.isFocused) onFocus?.invoke() },
            maxLines = maxLines,
            isError = state is TextFieldState.Error,
            trailingIcon = {
                if (isPassword) {
                    Image(
                        painter = painterResource(id = if (passwordVisible) R.drawable.eye else R.drawable.eye_off),
                        contentDescription = "",
                        modifier = Modifier.clickable(interactionSource = remember {
                            MutableInteractionSource()
                        }, indication = null, onClick = {
                            passwordVisible = !passwordVisible
                        })
                    )
                } else {
                    when (state) {
                        is TextFieldState.Error -> {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_error_outline_24_red),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.error)
                            )
                        }
                        is TextFieldState.Success -> {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MyColors.success)
                            )
                        }
                        TextFieldState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = MyColors.darkGray
                            )
                        }
                        else -> Unit
                    }
                }
            },
            singleLine = maxLines == 1,
            textStyle = LocalTextStyle.current.copy(
                fontSize = font.textSize,
                fontWeight = font.weight,
            ),
        )
        if (state is TextFieldState.Error && state.error != 0)
            Text(
                text = stringResource(id = state.error),
                color = MyColors.danger,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        else if (state is TextFieldState.Success && state.message != null)
            Text(
                text = stringResource(id = state.message),
                color = MyColors.success,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

    }
}



@Composable
fun CustomTextFieldWithErrorImage(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    maxLines: Int = Int.MAX_VALUE,
    state: TextFieldState = TextFieldState.None,
    isPassword: Boolean = false,
    enabled: Boolean =true,
    sameColorWhenDisable: Boolean = false,
    height: Dp? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    font: MyFont = MyFont.Body14,
    onFocus: (() -> Unit)? = null,
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        val focusedColor = MyColors.indigoPrimary
        OutlinedTextField(
            keyboardOptions = keyboardOptions,
            value = value,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = focusedColor,
                focusedBorderColor = focusedColor,
                errorBorderColor = MyColors.danger,
                errorLabelColor = MyColors.danger,
                unfocusedBorderColor = MyColors.gray10,
                disabledTextColor = if (sameColorWhenDisable) LocalContentColor.current.copy(
                    LocalContentAlpha.current) else LocalContentColor.current.copy(LocalContentAlpha.current)
                    .copy(ContentAlpha.disabled),

                disabledBorderColor = if (sameColorWhenDisable) MyColors.gray10 else focusedColor.copy(
                    ContentAlpha.disabled),
                disabledLabelColor = if (sameColorWhenDisable) MaterialTheme.colors.onSurface.copy(
                    ContentAlpha.medium) else MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                    .copy(ContentAlpha.disabled)
            ),
            visualTransformation = if (passwordVisible || !isPassword) VisualTransformation.None else PasswordVisualTransformation(),
            onValueChange = onValueChanged,
            enabled = enabled,
            label = {
                MyText(
                    text = placeholder,
                    font = font,
                    color = Color.Unspecified

                )
            },
            modifier = Modifier
                .let { mod ->
                    height?.let {
                        mod.height(it)
                    } ?: mod
                }
                .fillMaxWidth()
                .onFocusChanged { if (it.isFocused) onFocus?.invoke() },
            maxLines = maxLines,
            trailingIcon = {
                if (isPassword) {
                    Image(
                        painter = painterResource(id = if (passwordVisible) R.drawable.eye else R.drawable.eye_off),
                        contentDescription = "",
                        modifier = Modifier.clickable(interactionSource = remember {
                            MutableInteractionSource()
                        }, indication = null, onClick = {
                            passwordVisible = !passwordVisible
                        })
                    )
                } else {
                    when (state) {
                        is TextFieldState.Error -> {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_error_outline_24_red),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.error)
                            )
                        }
                        is TextFieldState.Success -> {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MyColors.success)
                            )
                        }
                        TextFieldState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = MyColors.darkGray
                            )
                        }
                        else -> Unit
                    }
                }
            },
            singleLine = maxLines == 1,
            textStyle = LocalTextStyle.current.copy(
                fontSize = font.textSize,
                fontWeight = font.weight,
            ),
        )
//        if (state is TextFieldState.Error && state.error != 0)
//            Text(
//                text = stringResource(id = state.error),
//                color = MyColors.danger,
//                style = MaterialTheme.typography.caption,
//                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
//            )
//        else if (state is TextFieldState.Success && state.message != null)
//            Text(
//                text = stringResource(id = state.message),
//                color = MyColors.success,
//                style = MaterialTheme.typography.caption,
//                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
//            )
    }
}
