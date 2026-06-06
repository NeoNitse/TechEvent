package com.sv.techevent.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sv.techevent.data.repository.EventRepository
import com.sv.techevent.domain.model.UIState
import com.sv.techevent.ui.catalog.CatalogScreen
import com.sv.techevent.ui.catalog.CatalogViewModel
import com.sv.techevent.ui.detail.DetailScreen
import com.sv.techevent.ui.detail.DetailViewModel
import com.sv.techevent.ui.favorites.FavoritesScreen
import com.sv.techevent.ui.favorites.FavoritesViewModel
import com.sv.techevent.ui.settings.SettingsScreen
import com.sv.techevent.ui.theme.ThemePreferences

sealed class Screen(val route: String) {
    object Catalog : Screen("catalog")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object Detail : Screen("detail/{eventId}") {
        fun createRoute(eventId: String) = "detail/$eventId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Catalog, "Eventos", Icons.Filled.DateRange),
    BottomNavItem(Screen.Favorites, "Favoritos", Icons.Filled.Favorite),
    BottomNavItem(Screen.Settings, "Configuración", Icons.Filled.Settings)
)

@Composable
fun AppNavigation(
    windowSizeClass: WindowSizeClass,
    repository: EventRepository,
    themePreferences: ThemePreferences
) {
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val navController = rememberNavController()

    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModel.factory(repository, themePreferences)
    )
    val catalogUiState by catalogViewModel.uiState.collectAsState()
    val isOffline by catalogViewModel.isOffline.collectAsState()

    if (isExpanded) {
        val detailViewModel: DetailViewModel = viewModel(
            factory = DetailViewModel.factory(repository)
        )
        ListDetailLayout(
            catalogViewModel = catalogViewModel,
            detailViewModel = detailViewModel,
            catalogUiState = catalogUiState,
            isOffline = isOffline,
            repository = repository,
            themePreferences = themePreferences
        )
    } else {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.screen.route,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(Screen.Catalog.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Catalog.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Catalog.route) {
                    CatalogScreen(
                        viewModel = catalogViewModel,
                        uiState = catalogUiState,
                        isOffline = isOffline,
                        onEventClick = { eventId ->
                            navController.navigate(Screen.Detail.createRoute(eventId))
                        }
                    )
                }
                composable(Screen.Favorites.route) {
                    val favoritesViewModel: FavoritesViewModel = viewModel(
                        factory = FavoritesViewModel.factory(repository)
                    )
                    val favUiState by favoritesViewModel.uiState.collectAsState()
                    FavoritesScreen(
                        uiState = favUiState,
                        onEventClick = { eventId ->
                            navController.navigate(Screen.Detail.createRoute(eventId))
                        },
                        onRemoveFavorite = { favoritesViewModel.removeFavorite(it) },
                        onExploreClick = {
                            navController.navigate(Screen.Catalog.route) {
                                popUpTo(Screen.Catalog.route) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(themePreferences = themePreferences)
                }
                composable(
                    route = Screen.Detail.route,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(200))
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(200))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) { entry ->
                    val eventId = entry.arguments?.getString("eventId") ?: return@composable
                    val detailViewModel: DetailViewModel = viewModel(
                        factory = DetailViewModel.factory(repository)
                    )
                    val events = (catalogUiState as? UIState.Success)?.data ?: emptyList()
                    DetailScreen(
                        eventId = eventId,
                        allEvents = events,
                        viewModel = detailViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}