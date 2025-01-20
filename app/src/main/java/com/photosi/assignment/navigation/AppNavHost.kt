package com.photosi.assignment.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.photosi.assignment.section.countries.SelectCountriesScreen
import com.photosi.assignment.section.upload.UploadImagesScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = AppRoute.SelectCountries,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        enterTransition = MaterialNavigationAnimation.enterTransition,
        exitTransition = MaterialNavigationAnimation.exitTransition,
        popEnterTransition = MaterialNavigationAnimation.popEnterTransition,
        popExitTransition = MaterialNavigationAnimation.popExitTransition
    ) {
        composable<AppRoute.SelectCountries> {
            SelectCountriesScreen(navController, koinViewModel())
        }
        composable<AppRoute.UploadImages> {
            UploadImagesScreen(navController, koinViewModel())
        }
    }
}