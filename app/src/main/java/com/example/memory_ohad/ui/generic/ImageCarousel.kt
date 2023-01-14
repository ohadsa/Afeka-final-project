package com.example.memory_ohad.ui.generic

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.memory_ohad.ui.theme.MyColors
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier,
    defaultIndex: Int,
    setDefault: Boolean = false,
    height: Dp? = null,
    width: Dp? = null
) {

    val state = rememberPagerState(images.size, defaultIndex)
    val scope = rememberCoroutineScope()
    if (setDefault) {
        scope.launch {
            state.scrollToPage(defaultIndex)
        }
    }


    Box(modifier = modifier) {
        Box(Modifier.align(Alignment.Center)) {
            HorizontalPager(state = state) { page ->
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ImageOrDefault(
                        imageUrl = images[page],
                        width = width,
                        height = height,
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Column(Modifier.align(Alignment.BottomCenter)) {
                DotsIndicator(
                    totalDots = images.size,
                    selectedIndex = state.currentPage,
                    selectedColor = Color.White,
                    unSelectedColor = MyColors.gray85
                )
                Spacer(modifier = Modifier.height(7.dp))
            }
        }
    }
}


@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) {
    if (totalDots < 2) return

    LazyRow(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()

    ) {
        items(totalDots) { index ->
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.height(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(
                            when (index) {
                                selectedIndex -> 5.dp
                                selectedIndex + 1, selectedIndex - 1 -> 4.dp
                                else -> 3.dp
                            }
                        )
                        .clip(CircleShape)
                        .background(if (index == selectedIndex) selectedColor else unSelectedColor)
                )

                if (index != totalDots - 1) {
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                }
            }
        }
    }
}