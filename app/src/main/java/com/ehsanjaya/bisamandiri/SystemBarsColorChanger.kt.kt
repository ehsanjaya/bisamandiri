package com.ehsanjaya.bisamandiri

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ContextCastToActivity")
@Composable
fun SystemBarsColorChanger(
    statusColor: Color?,
    navigationColor: Color?,
    isLightIcons: Boolean
) {
    val context = LocalContext.current
    val window = (context as Activity).window
    val view = LocalView.current

    SideEffect {
        statusColor?.let {
            window.statusBarColor = it.toArgb()
        }

        navigationColor?.let {
            window.navigationBarColor = it.toArgb()
        }

        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isLightIcons
    }
}