package com.ehsanjaya.bisamandiri.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ehsanjaya.bisamandiri.view_models.ImageViewModel
import com.ehsanjaya.bisamandiri.view_models.DescriptionViewModel

object Routes {
    const val Main = "Main"
    const val Camera = "Camera"
    const val Description = "Description"
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun NavigationGraph(
    navController: NavHostController,
    imageViewModel: ImageViewModel,
    descriptionViewModel: DescriptionViewModel
) {
    val insets = WindowInsets.systemBars
    val padding = insets.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val lifecycleOwner = LocalLifecycleOwner.current

    Scaffold (
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .systemBarsPadding()
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Main,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                    )
                }
            ) {
                composable(Routes.Main) {
                    Main(
                        navController = navController,
                        imageViewModel = imageViewModel,
                        descriptionViewModel = descriptionViewModel
                    )
                }
                composable(Routes.Camera) {
                    Camera(
                        navController = navController,
                        imageViewModel = imageViewModel,
                        descriptionViewModel = descriptionViewModel
                    )
                }
                composable(Routes.Description) {
                    Description(
                        navController = navController,
                        descriptionViewModel = descriptionViewModel
                    )
                }
            }
        }
    }
}