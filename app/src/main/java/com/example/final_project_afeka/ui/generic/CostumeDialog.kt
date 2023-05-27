package com.example.final_project_afeka.ui.generic
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.final_project_afeka.R
import com.example.final_project_afeka.ui.theme.MyColors
import com.example.final_project_afeka.ui.theme.generic.DrawableImage
import com.example.final_project_afeka.ui.theme.generic.MyText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun costumeDialog(
    context: Context,
    parentLayout: ViewGroup,
    variant : ToastVariant = ToastVariant.Info,
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    durationMillis: Int = 3000,
) {
    val toastComposeView = ComposeView(context)

    toastComposeView.setContent {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
        ) {
            CustomToastContent(
                variant = variant,
                message = message,
                actionLabel = actionLabel,
                onActionClick = onActionClick,
            )
        }
    }

    val snackBarView = FrameLayout(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP
        )
        // Set the top margin to 50dp
        val topMargin = 50f.dpToPx(context)
        (layoutParams as FrameLayout.LayoutParams).setMargins(0, topMargin.toInt(), 0, 0)

        addView(toastComposeView)
    }

    parentLayout.addView(snackBarView)

    CoroutineScope(Dispatchers.Main).launch {
        delay((durationMillis - 300).toLong())
        withContext(Dispatchers.Main) {
            val anim = AlphaAnimation(1.0f, 0.0f)
            anim.duration = 300
            snackBarView.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    parentLayout.removeView(snackBarView)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
        }
    }


}


@Composable
fun CustomToastContent(
    message: String,
    variant: ToastVariant = ToastVariant.Info,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)?
) {
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(46.dp)
                .align(Alignment.Center)
                .border(1.dp, variant.borderColor, RoundedCornerShape(4.dp))
                .background(
                    variant.background,
                    RoundedCornerShape(4.dp)
                ) ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                modifier = Modifier.padding(horizontal = 12.dp).weight(1f,false),
                verticalAlignment = Alignment.CenterVertically
                    ){
                variant.icon?.let {
                    DrawableImage(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(22.dp),
                        id = it,
                        svg = true
                    )
                }
                MyText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = message,
                    font = MyFont.ButtonSmall,
                    lineHeight = MyFont.ButtonSmall.lineHeight,
                    color = if (variant == ToastVariant.Info) Color.White else MyColors.darkGray
                )
            }
            actionLabel?.let { txt ->
                onActionClick?.let { action ->
                    MyText(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickableNoFeedback { action() },
                        text = txt,
                        font = MyFont.Body14,
                        color = MyColors.indigoPrimary
                    )
                }
            }

        }
    }
}

enum class ToastVariant(
    val borderColor: Color,
    val icon: Int?,
    val background: Color
) {
    Info(
        borderColor = MyColors.darkGray,
        icon = null,
        background = MyColors.darkGray
    ),
    Success(
        borderColor = MyColors.success25,
        icon = R.drawable.location_save,
        background = MyColors.success10
    ),
}

@Preview
@Composable
fun PreviewToast() {
    Column(modifier  = Modifier.padding(16.dp).background(Color.White)) {
        Spacer(modifier = Modifier.height(16.dp))
        CustomToastContent(
            message = "This is a toast",
            variant = ToastVariant.Success,
            actionLabel = "Success",
            onActionClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomToastContent(
            message = "This is a toast",
            variant = ToastVariant.Info,
            actionLabel = null,
            onActionClick = null
        )
        Spacer(modifier = Modifier.height(16.dp))

    }
}
fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
}