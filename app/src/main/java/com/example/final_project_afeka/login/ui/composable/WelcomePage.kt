package com.example.final_project_afeka.login.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.final_project_afeka.R
import com.example.final_project_afeka.login.ui.LoginMode
import com.example.final_project_afeka.login.ui.LoginViewModel
import com.example.final_project_afeka.ui.generic.*
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WelcomePage(
    viewModel: LoginViewModel,
    onLogInTapped: () -> Unit,
) {
    val user by viewModel.myUser.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val validEmail = user.email.validateEmail
    var confirmPassword by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    fun showSheet(show: Boolean) =
        scope.launch {
            if (show) sheetState.show()
            else sheetState.hide()
        }
    ModalBottomSheetLayout(
        sheetShape = MaterialTheme.shapes.large.copy(
            topStart = CornerSize(12.dp), topEnd = CornerSize(12.dp)
        ),
        sheetState = sheetState,
        sheetContent = {
            ChooseAvatarPage(
                onChoose = {
                    viewModel.updateUser(user.copy(avatar = it))
                    showSheet(false)
                },
                onBack = {
                    showSheet(false)
                }
            )

        },
    ) {
        Box(Modifier
            .fillMaxSize()) {
            DrawableImage(id = R.drawable.top_left_circles,
                svg = false,
                modifier = Modifier
                    .width(200.dp)
                    .height(144.dp)
                    .align(
                        TopStart))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween

            ) {
                Column(horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    MyText(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                            .align(CenterHorizontally),
                        text = stringResource(id = R.string.welcome),
                        font = MyFont.GiantHeading,
                        color = MyColors.indigoPrimary,
                        lineHeight = MyFont.GiantHeading.lineHeight)
                    if (mode == LoginMode.SIGNUP)
                        AvatarCell(user.avatar) {
                            showSheet(true)
                        }
                }

                Column(
                    horizontalAlignment = CenterHorizontally,
                    modifier = Modifier.weight(1f, false)
                ) {
                    if (mode == LoginMode.SIGNUP) {
                        CustomTextField(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = user.username,
                            placeholder = stringResource(id = R.string.userName),
                            onValueChanged = {
                                viewModel.updateUser(user.copy(username = it))
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    CustomTextFieldWithErrorImage(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = user.email,
                        placeholder = stringResource(id = R.string.email),
                        onValueChanged = {
                            viewModel.updateUser(user.copy(email = it))
                        },
                        state = if (validEmail) TextFieldState.Success(R.string.empty) else TextFieldState.Error(
                            R.string.empty),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextFieldWithErrorImage(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = user.password,
                        placeholder = stringResource(id = R.string.password),
                        onValueChanged = {
                            viewModel.updateUser(user.copy(password = it))
                        },
                        isPassword = true,
                    )
                    if (mode == LoginMode.SIGNUP) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomTextFieldWithErrorImage(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = confirmPassword,
                            placeholder = stringResource(id = R.string.confirm_password),
                            onValueChanged = {
                                confirmPassword = it
                            },
                            isPassword = true,
                        )
                    }


                }
                Column(Modifier
                    .padding(vertical = 36.dp, horizontal = 24.dp)
                    .fillMaxWidth()) {
                    ButtonV2(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = if (mode == LoginMode.LOGIN) stringResource(id = R.string.login) else stringResource(
                            id = R.string.sign_up),
                        enabled = if (mode == LoginMode.LOGIN) (validEmail && user.validPassword()) else user.allFilled(
                            confirmPassword),
                        variant = ButtonVariant.Primary,
                        onClick = {
                            onLogInTapped()
                        })
                    Spacer(modifier = Modifier.height(24.dp))
                    ButtonV2(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = if (mode == LoginMode.LOGIN) stringResource(id = R.string.sign_up) else stringResource(
                            id = R.string.login),
                        enabled = true,
                        variant = ButtonVariant.Secondary,
                        onClick = {
                            viewModel.changeModeTapped()
                        })
                }
            }
        }
    }
}

@Composable
fun AvatarCell(
    image: String,
    withEdit: Boolean = true,
    onTapped: () -> Unit = {},

    ) {
    Column {
        Box(modifier = Modifier
            .size(140.dp)
            .clickableNoFeedback {
                onTapped()
            }
            .clip(CircleShape)) {
            ImageOrDefault(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(4.dp, MyColors.indigoDark, CircleShape),
                imageUrl = image,
                width = 140.dp,
                height = 140.dp,
                defaultValue = R.drawable.user)

        }
        Spacer(modifier = Modifier.height(8.dp))
        if (withEdit) {
            MyText(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(CenterHorizontally),
                text = stringResource(id = R.string.tapToEdit),
                font = MyFont.Body16,
                color = MyColors.indigoDark,
                lineHeight = MyFont.Body14.lineHeight)
        }
    }
}

@Composable
fun ChooseAvatarPage(onChoose: (String) -> Unit, onBack: () -> Unit) {
    Column(Modifier
        .fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        MyText(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(16.dp),
            text = stringResource(id = R.string.chooseAvatar),
            font = MyFont.Heading5,
            lineHeight = MyFont.Heading5.lineHeight,
            textAlign = TextAlign.Center)

        Row(Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(12.dp)
            .horizontalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(24.dp))
            avatars.forEach {
                ImageOrDefault(modifier = Modifier
                    .align(CenterVertically)
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickableNoFeedback {
                        onChoose(it)
                    }, imageUrl = it, width = 80.dp, height = 80.dp)
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}



val String.validateEmail: Boolean
    get() = Pattern.compile(EMAIL_REGEX).matcher(this).find()


var EMAIL_REGEX = "^\\w+[\\w._%-+#]+@[\\w.-]+\\.[\\w]{2,6}$"


var avatars = listOf(
    "https://cdn1.iconfinder.com/data/icons/user-pictures/100/female1-512.png",
    "https://e7.pngegg.com/pngimages/312/283/png-clipart-man-s-face-avatar-computer-icons-user-profile-business-user-avatar-blue-face-thumbnail.png",
    "https://i.pinimg.com/originals/a6/58/32/a65832155622ac173337874f02b218fb.png",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQetdxnvvKgvsVlJItQBsZnPe1dB8-f_RrITpi3-elpAFUDz5k6-e-NhL83vWtgfKR0dDw&usqp=CAU",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT7gXPWxo_fQPzvP2TNGFtHzqQiChF6VklO68Fydsig_A69sVnizAMg_bxzCLvFEDMpwI8&usqp=CAU",
    "https://www.shareicon.net/data/512x512/2016/09/15/829472_man_512x512.png",
)
