package com.ehsanjaya.bisamandiri.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ehsanjaya.bisamandiri.Detector
import com.ehsanjaya.bisamandiri.MainActivity
import com.ehsanjaya.bisamandiri.R
import com.ehsanjaya.bisamandiri.ui.theme.dyslexicRegularFontFamily
import com.ehsanjaya.bisamandiri.utilites.IconButtonShimmer
import com.ehsanjaya.bisamandiri.utilites.shimmerEffect
import com.ehsanjaya.bisamandiri.view_models.ImageViewModel
import com.ehsanjaya.bisamandiri.view_models.DescriptionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Main(
    navController: NavController,
    imageViewModel: ImageViewModel,
    descriptionViewModel: DescriptionViewModel
) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val cameraPermissionState: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var textToSpeak by remember { mutableStateOf("") }
    var speakingText by remember { mutableStateOf("") }

    var buttonCameraVisibility by remember { mutableStateOf(false) }
    var buttonFeatureVisibility by remember { mutableStateOf(false) }
    val blurRadius by remember { mutableStateOf(Animatable(25f)) }
    var imageSize by remember { mutableStateOf(IntSize(0, 0)) }

    LaunchedEffect(key1 = Unit) {
        buttonCameraVisibility = false
        buttonFeatureVisibility = false
        delay(500)
        buttonCameraVisibility = true
        if (descriptionViewModel.visibleButtonStart.value) {
            delay(500)
            buttonFeatureVisibility = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF050404)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        imageViewModel.imageDetected.value?.let {
            LaunchedEffect(key1 = Unit) {
                blurRadius.snapTo(targetValue = 25f)
                blurRadius.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = LinearEasing
                    )
                )
            }
            Box(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color(0xFF9BFFD0),
                            ambientColor = Color.Black
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .shimmerEffect()
                ) {
                    Box(modifier = Modifier.padding(5.dp)) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Image",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = Color(0xFF050404))
                                .blur(
                                    radiusX = blurRadius.value.dp,
                                    radiusY = blurRadius.value.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                                .onGloballyPositioned { coordinates ->
                                    imageSize = coordinates.size
                                }
                        )
                        val density = LocalDensity.current
                        val predictions = imageViewModel.predictions.value

                        predictions.bestBoxes.forEach { item ->
                            val type = item.className
                            val location = RectF(
                                item.x1,
                                item.y1,
                                item.x2,
                                item.y2
                            )

                            Button(
                                onClick = {
                                    textToSpeak = "Ini adalah ${item.className.lowercase()}."

                                    if (tts.value == null) {
                                        speakingText = ""

                                        tts.value = TextToSpeech(context) { status ->
                                            if (status == TextToSpeech.SUCCESS) {
                                                tts.value?.setLanguage(Locale("id", "ID"))
                                                tts.value?.setSpeechRate(0.7f)

                                                tts.value?.setOnUtteranceProgressListener(object :
                                                    UtteranceProgressListener() {
                                                    override fun onStart(utteranceId: String?) {
                                                        Handler(Looper.getMainLooper()).post {
                                                            speakingText = ""
                                                        }
                                                    }

                                                    override fun onDone(utteranceId: String?) {
                                                        Handler(Looper.getMainLooper()).post {
                                                            speakingText = textToSpeak
                                                            descriptionViewModel.type.value = type
                                                            descriptionViewModel.visibleButtonStart.value =
                                                                true
                                                            buttonFeatureVisibility = true
                                                        }
                                                    }

                                                    override fun onError(utteranceId: String?) {
                                                        Handler(Looper.getMainLooper()).post {
                                                            speakingText = ""
                                                        }
                                                    }

                                                    // This gives word-by-word progress (API 26+)
                                                    override fun onRangeStart(
                                                        utteranceId: String?,
                                                        start: Int,
                                                        end: Int,
                                                        frame: Int
                                                    ) {
                                                        Handler(Looper.getMainLooper()).post {
                                                            speakingText =
                                                                textToSpeak.substring(0, end)
                                                        }
                                                    }
                                                })
                                            }
                                        }
                                    }

                                    val params = Bundle()
                                    params.putString(
                                        TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                                        "utteranceId"
                                    )
                                    tts.value?.speak(
                                        textToSpeak,
                                        TextToSpeech.QUEUE_FLUSH,
                                        params,
                                        "utteranceId"
                                    )
                                },
                                modifier = Modifier
                                    .size(
                                        width = with(density) {
                                            location
                                                .width()
                                                .toDp() * imageSize.width
                                        },
                                        height = with(density) {
                                            location
                                                .height()
                                                .toDp() * imageSize.height
                                        }
                                    )
                                    .offset(
                                        x = with(density) { location.left.toDp() * imageSize.width },
                                        y = with(density) { location.top.toDp() * imageSize.height }
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Transparent
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play Circle",
                                    modifier = Modifier
                                        .size(35.dp)
                                        .clip(shape = CircleShape)
                                        .alpha(0.5f)
                                        .shimmerEffect(),
                                    tint = Color.White,
                                )
                            }
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "Sparkle",
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.TopEnd)
                        .offset(
                            x = 14.dp,
                            y = (-14).dp
                        ),
                    tint = Color.White
                )
                IconButtonShimmer(
                    modifier = Modifier.align(Alignment.BottomStart),
                    onClick = {
                        imageViewModel.image.value = null
                        imageViewModel.imageDetected.value = null
                        imageViewModel.predictions.value = Detector.DetectionResult(emptyList(), 0)
                    },
                    icon = Icons.Filled.Delete,
                    iconColor = Color.White
                )
            }
            AnimatedVisibility(
                visible = speakingText != "",
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(45.dp)
                            .clip(shape = CircleShape)
                            .background(color = Color.White)
                    )
                    Text(
                        text = speakingText,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontFamily = dyslexicRegularFontFamily,
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.White
                    )
                }
            }
        } ?: run {
            Text(
                text = "No photo captured",
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                fontFamily = dyslexicRegularFontFamily,
                modifier = Modifier.padding(bottom = 10.dp),
                color = Color.White
            )
        }
        AnimatedVisibility(
            visible = buttonCameraVisibility
        ) {
            Box {
                Button(
                    onClick = {
                        if (cameraPermissionState.status.isGranted) {
                            navController.navigate(Routes.Camera) {
                                popUpTo(Routes.Camera) {
                                    inclusive = true
                                }
                            }
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .padding(5.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(5.dp),
                            spotColor = Color(0xFF9BFFD0),
                            ambientColor = Color.Black
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D9057),
                        contentColor = Color(0xFFFDF8F7)
                    ),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Text(
                        text = "Ambil Foto",
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontFamily = dyslexicRegularFontFamily,
                        color = Color.White
                    )
                }
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "Photo Camera",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .offset(
                            x = 10.dp,
                            y = (-10).dp
                        ),
                    tint = Color.White
                )
            }
        }
        AnimatedVisibility(
            visible = buttonFeatureVisibility
        ) {
            Box {
                Button(
                    onClick = {
                        navController.navigate(Routes.Description) {
                            popUpTo(Routes.Description) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .padding(5.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(5.dp),
                            spotColor = Color(0xFF9BFFD0),
                            ambientColor = Color.Black
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D9057),
                        contentColor = Color(0xFFFDF8F7)
                    ),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Text(
                        text = "Ciri-Ciri",
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontFamily = dyslexicRegularFontFamily,
                        color = Color.White
                    )
                }
                Icon(
                    imageVector = Icons.Filled.PlayCircle,
                    contentDescription = "Play Circle",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                        .offset(
                            x = 10.dp,
                            y = (-10).dp
                        ),
                    tint = Color.White
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Text(
            text = "Copyright - AInovators©, 2025",
            fontSize = 10.sp,
            fontFamily = dyslexicRegularFontFamily,
            color = Color(0xFF8D8E90),
            modifier = Modifier
                .padding(
                    top = 45.dp,
                    bottom = 15.dp,
                    start = 15.dp,
                    end = 15.dp
                )
        )
    }
}
