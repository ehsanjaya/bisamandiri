package com.ehsanjaya.bisamandiri.utilites

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmerEffect(
    colors: List<Color> = listOf(
        Color(0xFF1D9057),
        Color(0xFF51DA94),
        Color(0xFF1D9057)
    ),
    duration: Int = 1500
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "")

    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(duration)
        ),
        label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned { coordinates ->
        size = coordinates.size
    }
}

fun Brush.Companion.gradientEffect(
    colors: List<Color> = listOf(
        Color(0xFF3573EE),
        Color(0xFFFFFFFF),
        Color(0xFF3573EE)
    ),
    size: Size
): Brush {
    return Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )
}

@Composable
fun shimmerEffect(
    colors: List<Color> = listOf(
        Color(0xFFC0FF72),
        Color(0xFFE3FFC4),
        Color(0xFFC0FF72)
    ),
    duration: Int = 1500
): Brush {
    val size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "")

    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(duration)
        ),
        label = ""
    )

    return if (size == IntSize.Zero) {
        Brush.linearGradient(colors)
    } else {
        Brush.linearGradient(
            colors = colors,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    }
}