package com.example.final_project_afeka.ui.theme.generic


import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import com.example.final_project_afeka.ui.generic.MyFont
import com.example.final_project_afeka.ui.theme.MyColors

@Composable
fun DefaultLinkifyText(
    text: String?,
    modifier: Modifier = Modifier,
    color: Color = MyColors.darkGray,
    font: MyFont = MyFont.Body14,
    maxLines: Int? = null,
    onClick: (() -> Unit)? = null,
) {
    val ctx = LocalContext.current
    val tv = remember {
        TextView(ctx)
    }
    AndroidView(modifier = modifier, factory = { tv }) { textView ->
        textView.text = text
        textView.setTextColor(color.toArgb())
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, font.textSize.value)
        textView.setLinkTextColor(MyColors.indigoPrimary.toArgb())
        if (onClick != null)
            textView.setOnClickListener { onClick() }
        textView.typeface = font.getTypeface(ctx)

        if (maxLines != null)
            textView.maxLines = maxLines
        LinkifyCompat.addLinks(textView, Linkify.WEB_URLS)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
//overflow = TextOverflow.Ellipsis

@Composable
fun MyText(
    text: String,
    modifier: Modifier = Modifier,
    font: MyFont = MyFont.Body14,
    color: Color = Color.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    MyText(
        text = text, modifier = modifier,
        font = font,
        color = color,
        lineHeight = lineHeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Clip
    )
}

@Composable
fun MyText(
    text: String,
    modifier: Modifier = Modifier,
    font: MyFont = MyFont.Body14,
    color: Color = Color.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        fontSize = font.textSize,
        fontWeight = font.weight,
        fontFamily = font.fontName.toFamily(),
        color = color,
        modifier = modifier,
        lineHeight = lineHeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        style = if (font.fontStyle == FontStyle.Italic)
            LocalTextStyle.current.copy(fontStyle = FontStyle.Italic)
        else
            LocalTextStyle.current,
    )
}

@Composable
fun MyText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    font: MyFont = MyFont.Body14,
    color: Color = Color.Black,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        fontSize = font.textSize,
        fontWeight = font.weight,
        color = color,
        modifier = modifier,
        lineHeight = lineHeight,
        textAlign = textAlign
    )
}

@Composable
fun MyText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    font: MyFont = MyFont.Body14,
    color: Color = Color.Black,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        fontSize = font.textSize,
        fontWeight = font.weight,
        fontFamily = font.fontName.toFamily(),
        color = color,
        modifier = modifier,
        lineHeight = lineHeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        style = if (font.fontStyle == FontStyle.Italic)
            LocalTextStyle.current.copy(fontStyle = FontStyle.Italic)
        else
            LocalTextStyle.current,
    )
}
