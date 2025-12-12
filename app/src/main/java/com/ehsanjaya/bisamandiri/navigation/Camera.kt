package com.ehsanjaya.bisamandiri.navigation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.media.MediaActionSound
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.ehsanjaya.bisamandiri.Detector
import com.ehsanjaya.bisamandiri.MainActivity
import com.ehsanjaya.bisamandiri.utilites.IconButtonShimmer
import com.ehsanjaya.bisamandiri.utilites.setRotationBitmap
import com.ehsanjaya.bisamandiri.utilites.shimmerEffect
import com.ehsanjaya.bisamandiri.view_models.ImageViewModel
import com.ehsanjaya.bisamandiri.view_models.DescriptionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalGetImage::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Camera(
    navController: NavController,
    imageViewModel: ImageViewModel,
    descriptionViewModel: DescriptionViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? MainActivity

    val sound = remember { MediaActionSound() }
    val textMeasurer = rememberTextMeasurer()
    val scope = rememberCoroutineScope()

    var predictions: Detector.DetectionResult by remember { mutableStateOf(Detector.DetectionResult(emptyList(), 0)) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var imageDetected: Bitmap by remember { mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) }
    var inferenceTime by remember { mutableStateOf("100ms") }
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var previewSize by remember { mutableStateOf(Size.Zero) }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            imageAnalysisOutputImageFormat = ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
            //setImageAnalysisTargetSize(CameraController.OutputSize(android.util.Size(previewSize.width.toInt(), previewSize.height.toInt())))
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS or CameraController.IMAGE_CAPTURE)
        }
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val outlineButtonSize = 80f
    val buttonPadding = 16f
    val buttonSize = remember { Animatable(outlineButtonSize) }
    val buttonY = remember { Animatable(200f) }

    val zoomState by cameraController.zoomState.observeAsState()

    LaunchedEffect(key1 = Unit) {
        cameraController.setImageAnalysisAnalyzer(cameraExecutor) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val bitmapBuffer =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )

            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)}
            imageProxy.close()

            predictions = activity?.detect(bitmapBuffer.setRotationBitmap(isFrontCamera))!!

            imageViewModel.image.value = bitmapBuffer.setRotationBitmap(isFrontCamera)
            imageViewModel.imageSize.value = android.util.Size(
                bitmapBuffer.setRotationBitmap(isFrontCamera).width,
                bitmapBuffer.setRotationBitmap(isFrontCamera).height
            )
            imageDetected = activity.imageDetected(bitmapBuffer.setRotationBitmap(isFrontCamera), predictions)
            inferenceTime = "${predictions.inferenceTime}ms"
        }
    }

    LaunchedEffect(key1 = Unit) {
        delay(100)
        buttonY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 850,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .background(color = Color(0xFF050404))
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inferenceTime,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                LinearProgressIndicator(
                    progress = {
                        if (zoomState != null) {
                            val minZoom = zoomState!!.minZoomRatio
                            val maxZoom = zoomState!!.maxZoomRatio
                            val currentZoom = zoomState!!.zoomRatio
                            ((currentZoom - minZoom) / (maxZoom - minZoom)).coerceIn(0f, 1f)
                        } else 0f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 5.dp,
                            bottom = 5.dp
                        ),
                    color = Color(0xFF1D9057),
                    trackColor = Color(0xFF2B2D30)
                )
                Row(
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        )
                        .background(color = Color(0xFF050404))
                        .fillMaxWidth()
                        .height(95.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButtonShimmer(
                        onClick = {
                            navController.navigate(Routes.Main) {
                                popUpTo(Routes.Main) {
                                    inclusive = true
                                }
                            }
                        },
                        icon = Icons.Filled.Close,
                        iconColor = Color.White
                    )
                    OutlinedButton(
                        modifier = Modifier
                            .size(outlineButtonSize.dp)
                            .offset(
                                x = 0.dp,
                                y = buttonY.value.dp
                            )
                            .shadow(
                                elevation = 5.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF1D9057),
                                ambientColor = Color(0xFF1D9057)
                            ),
                        onClick = {
                            val mainExecutor = ContextCompat.getMainExecutor(context)
                            scope.launch {
                                cameraController.clearImageAnalysisAnalyzer()
                                cameraController.unbind()

                                descriptionViewModel.visibleButtonStart.value = false
                                sound.play(MediaActionSound.SHUTTER_CLICK)
                                buttonSize.animateTo(
                                    targetValue = outlineButtonSize - 10f,
                                    animationSpec = tween(
                                        durationMillis = 100,
                                        easing = LinearEasing
                                    )
                                )
                                buttonSize.animateTo(
                                    targetValue = outlineButtonSize,
                                    animationSpec = tween(
                                        durationMillis = 100,
                                        easing = LinearEasing
                                    )
                                )
                                delay(100)
                                imageViewModel.predictions.value = predictions
                                imageViewModel.imageDetected.value = imageDetected

                                navController.navigate(Routes.Main) {
                                    popUpTo(Routes.Main) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        shape = CircleShape,
                        border = BorderStroke(
                            width = 4.dp,
                            color = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size((buttonSize.value - buttonPadding).dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                    }
                    IconButtonShimmer(
                        onClick = {
                            if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                                cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                            isFrontCamera = cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
                        },
                        icon = Icons.Filled.Flip,
                        iconColor = Color.White
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            AndroidView(
                modifier = Modifier
                    .alpha(0f)
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        previewSize = Size(
                            width = coordinates.size.width.toFloat(),
                            height = coordinates.size.height.toFloat()
                        )
                    },
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        setBackgroundColor(0)
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }.also { previewView ->
                        cameraController.bindToLifecycle(lifecycleOwner)
                        previewView.controller = cameraController
                    }
                }
            )
            Image(
                bitmap = imageDetected.asImageBitmap(),
                contentDescription = "Detected objects",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        canvasSize = coordinates.size
                    },
            )
            /*
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                imageViewModel.predictions.value.bestBoxes.forEach { item ->
                    val confidence = item.confidence
                    val location = RectF(
                        item.x1 * canvasSize.width,
                        item.y1 * canvasSize.height,
                        item.x2 * canvasSize.width,
                        item.y2 * canvasSize.height
                    )
                    val label = item.className

                    val textLayoutResult = textMeasurer.measure(
                        text = AnnotatedString("$label $confidence"),
                        style = TextStyle(
                            color = Color(0xFF3573EE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    drawRect(
                        color = Color(0xFFFDF8F7),
                        topLeft = Offset(
                            x = location.left,
                            y = location.top
                        ),
                        size = Size(
                            width = location.width(),
                            height = location.height()
                        ),
                        style = Stroke(width = 6f)
                    )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = location.left,
                            y = (location.top  - textLayoutResult.size.height - 5).coerceAtLeast(0f)
                        )
                    )
                }
            }
            */
        }
    }
}
