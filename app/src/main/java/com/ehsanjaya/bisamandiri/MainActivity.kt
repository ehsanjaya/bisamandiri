package com.ehsanjaya.bisamandiri

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ehsanjaya.bisamandiri.navigation.NavigationGraph
import com.ehsanjaya.bisamandiri.ui.theme.BisaMandiriTheme
import com.ehsanjaya.bisamandiri.Constants.LABELS_PATH
import com.ehsanjaya.bisamandiri.Constants.MODEL_PATH
import com.ehsanjaya.bisamandiri.view_models.ImageViewModel
import com.ehsanjaya.bisamandiri.view_models.DescriptionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var detector: Detector? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detector = Detector(this, MODEL_PATH, LABELS_PATH) {
            toast(it, this)
        }

        enableEdgeToEdge()
        setContent {
            BisaMandiriTheme {
                com.ehsanjaya.bisamandiri.Preview()
            }
        }
    }

    fun detect(bitmapBuffer: Bitmap): Detector.DetectionResult {
        return detector?.detect(bitmapBuffer) ?: Detector.DetectionResult(emptyList(), 0)
    }

    fun imageDetected(bitmapBuffer: Bitmap, predictions: Detector.DetectionResult): Bitmap {
        val canvas = Canvas(bitmapBuffer)

        val boxPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = bitmapBuffer.width.toFloat() / 160f
            isAntiAlias = true
        }

        predictions.bestBoxes.forEach { item ->
            val confidence = item.confidence
            val location = RectF(
                item.x1 * bitmapBuffer.width,
                item.y1 * bitmapBuffer.height,
                item.x2 * bitmapBuffer.width,
                item.y2 * bitmapBuffer.height
            )
            val label = item.className

            val customTypeface = ResourcesCompat.getFont(baseContext, R.font.dyslexic_regular)
            val textPaint = Paint().apply {
                color = getColorDetector(item.indexClass)
                textSize = bitmapBuffer.width.toFloat() / 40f
                typeface = Typeface.create(customTypeface, Typeface.BOLD)
                isFakeBoldText = true
                isAntiAlias = true
            }

            canvas.drawRoundRect(location, 5f, 5f, boxPaint)
            canvas.drawText(
                "$label $confidence",
                location.left,
                location.top - bitmapBuffer.width.toFloat() / 80f,
                textPaint
            )
        }

        return bitmapBuffer
    }

    private fun getColorDetector(index: Int): Int {
        val colors = listOf(
            0xFF74CC00,
            0xFF1F48FF,
            0xFFFF3131,
            0xFF99F0FF
        )
        return colors[index].toInt()
    }

    private fun toast(message: String, context: Context) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

}
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Preview() {
    BisaMandiriTheme {
        val navController = rememberNavController()
        val imageViewModel: ImageViewModel = viewModel()
        val descriptionViewModel: DescriptionViewModel = viewModel()

        SystemBarsColorChanger(
            statusColor = Color(0xFF050404),
            navigationColor = Color(0xFF050404),
            isLightIcons = false
        )

        NavigationGraph(
            navController = navController,
            imageViewModel = imageViewModel,
            descriptionViewModel = descriptionViewModel
        )
    }
}