package com.kltn.anigan.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.kltn.anigan.routes.Routes

@Composable
fun AniganNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.MAIN_SCREEN.route
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
            "${Routes.EDIT_SCREEN.route}?uri={uri}&editType={editType}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("editType") { type = NavType.StringType },
            )
        ) {backStackEntry ->
            EditScreen(
                navController,
                backStackEntry.arguments?.getString("uri"),
                backStackEntry.arguments?.getString("editType")
            )
        }

        composable(
            "${Routes.FILL_TEXT_TOOL.route}?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) {backStackEntry ->
            FillTextScreen(navController = navController, uri = backStackEntry.arguments?.getString("uri"))
        }

        composable(Routes.BG_REMOVER_TOOL.route) {
            BGRemoverScreen(navController)
        }

        composable(Routes.COLLAGE_TOOL.route) {
             CollageScreen(navController)
        }
    }
}