package com.ehsanjaya.bisamandiri.utilites

import android.graphics.drawable.Icon
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IconButtonShimmer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    iconColor: Color = Color.White,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    size: Dp = 55.dp
) {
    val scope = rememberCoroutineScope()
    val buttonSize = remember { Animatable(size.value) }
    var debounce by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .then(modifier)
            .size(size)
    ) {
        IconButton(
            onClick = {
                if (debounce) {
                    debounce = false
                    scope.launch {
                        buttonSize.animateTo(
                            targetValue = size.value - 10f,
                            animationSpec = tween(
                                durationMillis = 100,
                                easing = LinearEasing
                            )
                        )
                        onClick.invoke()
                        buttonSize.animateTo(
                            targetValue = size.value,
                            animationSpec = tween(
                                durationMillis = 100,
                                easing = LinearEasing
                            )
                        )
                        delay(1000)
                        debounce = true
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(buttonSize.value.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .shimmerEffect()
                .shadow(
                    elevation = 5.dp,
                    shape = shape,
                    spotColor = Color(0xFF1D9057),
                    ambientColor = Color.Black
                )
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "Icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    tint = iconColor
                )
            }
        }
    }
}