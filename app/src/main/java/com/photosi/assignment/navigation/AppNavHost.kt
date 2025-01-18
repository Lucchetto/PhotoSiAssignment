package com.photosi.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.photosi.assignment.section.countries.SelectCountriesScreen
import com.photosi.assignment.section.upload.UploadImagesScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = AppRoute.SelectCountries) {
        composable<AppRoute.SelectCountries> {
            SelectCountriesScreen(navController, koinViewModel())
        }
        composable<AppRoute.UploadImages> {
            UploadImagesScreen(navController, koinViewModel())
        }
    }
}