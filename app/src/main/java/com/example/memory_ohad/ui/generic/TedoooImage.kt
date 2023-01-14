package com.example.memory_ohad.ui.theme.generic

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun DrawableImage(
    @DrawableRes id: Int,
    svg: Boolean,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
) {
    if (svg || LocalInspectionMode.current) {
        Image(
            painter = painterResource(id = id), contentDescription = null,
            modifier = modifier,
            colorFilter = colorFilter,
            contentScale = contentScale
        )
    } else
        GlideImage(
            imageModel = { id },
            imageOptions = ImageOptions(colorFilter = colorFilter, contentScale = contentScale),
            modifier = modifier
        )

}