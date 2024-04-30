package com.kltn.anigan.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kltn.anigan.routes.Routes

@Composable
fun AniganNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.MAIN_SCREEN.route,
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.MAIN_SCREEN.route) {
            MainScreen(navController)
        }
        composable(Routes.AI_TOOLS.route) { 
            AIToolScreen(navController)
        }
        composable(
            "${Routes.AI_RESULT_SCREEN.route}?numGenerations={num}&userUri={userUri}",
            arguments = listOf(
                navArgument("num") { type = NavType.IntType },
                navArgument("userUri") { type = NavType.StringType },
            )
        ) {backStackEntry ->
            AIResultScreen(navController,
                backStackEntry.arguments?.getInt("num"),
                backStackEntry.arguments?.getString("userUri")
            )
        }
        composable(
            "${Routes.EDIT_SCREEN.route}?uri={uri}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
            )
        ) {backStackEntry ->
            EditScreen(
                navController,
                backStackEntry.arguments?.getString("uri"),
            )
        }

        composable(
            "${Routes.BRUSH_SCREEN.route}?uri={uri}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
            )
        ) {backStackEntry ->
            BrushScreen(
                navController,
                backStackEntry.arguments?.getString("uri"),
            )
        }

        composable(
            "${Routes.ADD_TEXT.route}?uri={uri}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
            )
        ) {backStackEntry ->
            AddTextScreen(
                navController,
                backStackEntry.arguments?.getString("uri"),
            )
        }

        composable(
            "${Routes.FILTER_TOOL.route}?uri={uri}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
            )
        ) {backStackEntry ->
            FilterScreen(
                navController,
                backStackEntry.arguments?.getString("uri"),
            )
        }
    }
}