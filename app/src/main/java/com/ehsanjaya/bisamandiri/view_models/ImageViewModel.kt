package com.ehsanjaya.bisamandiri.view_models

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Size
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.ehsanjaya.bisamandiri.Detector

class ImageViewModel : ViewModel() {
    @SuppressLint("AutoboxingStateCreation")
    var image: MutableState<Bitmap?> = mutableStateOf(null)
    var imageDetected: MutableState<Bitmap?> = mutableStateOf(null)
    var predictions: MutableState<Detector.DetectionResult> = mutableStateOf(Detector.DetectionResult(emptyList(), 0))
    var imageSize: MutableState<Size> = mutableStateOf(Size(0, 0))
}