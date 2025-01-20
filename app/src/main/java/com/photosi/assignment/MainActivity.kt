package com.photosi.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.photosi.assignment.navigation.AppNavHost
import com.photosi.assignment.ui.theme.PhotoSìAssignmentTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoSìAssignmentTheme {
                AppNavHost(koinViewModel())
            }
        }
    }
}