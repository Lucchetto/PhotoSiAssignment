package com.photosi.assignment.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.PathEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import kotlin.math.roundToInt

/**
 * Animations for [NavHost] that replicate Android 14 system activity animations
 */
internal object MaterialNavigationAnimation {

    private const val SLIDE_ANIMATION_DURATION = 810
    private val easingEmphasizedInterpolator get() = PathEasing(
        Path().apply {
            moveTo(0f, 0f)
            cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
            cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
        }
    )
    private val slideAnimationSpec get() = tween<IntOffset>(
        durationMillis = SLIDE_ANIMATION_DURATION,
        easing = easingEmphasizedInterpolator
    )
    private val fadeTopLayerAnimationSpec get() = tween<Float>(
        durationMillis = 83,
        delayMillis = 90,
        easing = LinearEasing
    )
    private val fadeBottomLayerAnimationSpec get() = tween<Float>(
        durationMillis = 83,
        delayMillis = 90,
        easing = LinearEasing
    )

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            animationSpec = slideAnimationSpec,
            initialOffsetX = { (0.1f * it).roundToInt() }
        ) + fadeIn(animationSpec = fadeTopLayerAnimationSpec)
    }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            animationSpec = slideAnimationSpec,
            targetOffsetX = { (-0.1f * it).roundToInt() }
        ) + fadeOut(animationSpec = fadeBottomLayerAnimationSpec)
    }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            animationSpec = slideAnimationSpec,
            initialOffsetX = { (-0.1f * it).roundToInt() }
        ) + fadeIn(animationSpec = fadeBottomLayerAnimationSpec)
    }

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            animationSpec = slideAnimationSpec,
            targetOffsetX = { (0.1f * it).roundToInt() }
        ) + fadeOut(animationSpec = fadeTopLayerAnimationSpec)
    }
}