package com.pallav.drawonme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pallav.drawonme.presentation.navigation.DrawOnMeApp
import com.pallav.drawonme.ui.theme.DrawOnMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawOnMeTheme {
                DrawOnMeApp()
            }
        }
    }
}