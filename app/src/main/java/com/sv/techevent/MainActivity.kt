package com.sv.techevent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sv.techevent.di.AppContainer
import com.sv.techevent.ui.navigation.AppNavigation
import com.sv.techevent.ui.theme.TechEventTheme
import com.sv.techevent.ui.theme.ThemePreferences
import com.sv.techevent.ui.catalog.CatalogViewModel

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appContainer = (application as TechEventApp).container

        val themePreferences = ThemePreferences(appContainer.dataStore)

        setContent {
            val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = false)
            val windowSizeClass = calculateWindowSizeClass(this)

            TechEventTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    windowSizeClass = windowSizeClass,
                    repository = appContainer.eventRepository,
                    themePreferences = themePreferences
                )
            }
        }
    }
}