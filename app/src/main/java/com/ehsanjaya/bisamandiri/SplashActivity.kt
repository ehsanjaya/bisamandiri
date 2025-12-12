package com.ehsanjaya.bisamandiri

import kotlin.math.*
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ehsanjaya.bisamandiri.ui.theme.BisaMandiriTheme
import com.ehsanjaya.bisamandiri.ui.theme.dyslexicRegularFontFamily
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BisaMandiriTheme {
                SystemBarsColorChanger(
                    statusColor = Color(0xFFC1FF72),
                    navigationColor = Color(0xFFC1FF72),
                    isLightIcons = false
                )
                SplashScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseOfNonLambdaOffsetOverload")
    @Preview(showBackground = true)
    @Composable
    private fun SplashScreen() {
        val imageSize = 140f

        var circleVisible by remember { mutableStateOf(false) }
        var textVisible by remember { mutableStateOf(false) }
        val angle = remember { Animatable(0f) }
        val radius = remember { Animatable(0f) }

        var imageVisible by remember { mutableStateOf(false) }
        val boxTransparent = remember { Animatable(1f) }

        val images = listOf(
            ImageData(
                image = R.drawable.ai_object_icon,
                text = "Detect"
            ),
            ImageData(
                image = R.drawable.interactive_icon,
                text = "Interact"
            ),
            ImageData(
                image = R.drawable.ai_generative_icon,
                text = "AI"
            ),
            ImageData(
                image = R.drawable.reading_book_icon,
                text = "Learn"
            ),
            ImageData(
                image = R.drawable.font_adjusment_icon,
                text = "Dyslexia"
            ),
            ImageData(
                image = R.drawable.ai_voice_icon,
                text = "Voice"
            ),
            ImageData(
                image = R.drawable.no_wifi_icon,
                text = "FreeToUse"
            ),
            ImageData(
                image = R.drawable.camera_icon,
                text = "CameraX"
            )
        )

        SystemBarsColorChanger(
            statusColor = Color(0xFF050404),
            navigationColor = Color(0xFF050404),
            isLightIcons = false
        )

        LaunchedEffect(key1 = true, block = {
            boxTransparent.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearEasing
                )
            )
            imageVisible = true
            delay(1200)
            circleVisible = true
            delay(500)
            radius.animateTo(
                targetValue = 145f,
                animationSpec = tween(
                    durationMillis = 1600,
                    easing = FastOutSlowInEasing
                )
            )
            textVisible = true
            delay(1500)
            textVisible = false
            delay(500)
            angle.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 1500,
                    easing = FastOutSlowInEasing
                )
            )
            delay(500)
            radius.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 1600,
                    easing = FastOutSlowInEasing
                )
            )
            delay(150)
            circleVisible = false
            imageVisible = false
            delay(500)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        })

        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF050404)),
            contentAlignment = Alignment.Center
        ) {
            if (circleVisible) {
                images.forEachIndexed { index, data ->
                    val angleDeg = (360f / images.size) * index + angle.value
                    val angleRad = Math.toRadians(angleDeg.toDouble())

                    Column(
                        modifier = Modifier
                            .offset(
                                x = (cos(angleRad) * radius.value).dp,
                                y = (sin(angleRad) * radius.value).dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = data.image),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(55.dp)
                                .clip(shape = CircleShape)
                                .background(color = Color.White)
                        )
                        AnimatedVisibility(
                            visible = textVisible
                        ) {
                            Text(
                                text = data.text,
                                fontSize = 10.sp,
                                fontFamily = dyslexicRegularFontFamily,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            AnimatedVisibility (
                visible = imageVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(animationSpec = tween(durationMillis = 1000)),
                exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(animationSpec = tween(durationMillis = 1000))
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size((imageSize + radius.value / 4).dp)
                            .background(color = Color(0xFF111111))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size((imageSize + radius.value / 8).dp)
                            .background(color = Color(0xFF1C1C1C))
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(shape = CircleShape)
                            .size(imageSize.dp)
                            .background(color = Color.White)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = boxTransparent.value))
        )
    }
}

private data class ImageData(
    val image: Int,
    val text: String
)
