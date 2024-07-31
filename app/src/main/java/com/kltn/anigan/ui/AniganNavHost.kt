package com.kltn.anigan.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kltn.anigan.domain.BillingViewModel
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.routes.Routes

@Composable
fun AniganNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.MAIN_SCREEN.route,
    viewModel: DocsViewModel,
    billingViewModel: BillingViewModel
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.MAIN_SCREEN.route) {
            MainScreen(navController, viewModel)
        }
        composable(Routes.LOGIN.route) {
            LoginScreen(navController, viewModel)
        }
        composable(Routes.SIGN_UP.route) {
            SignUpScreen(navController, viewModel)
        }
        composable(Routes.PROFILE.route) {
            ProfileScreen(navController = navController, viewModel = viewModel)
        }
        composable(Routes.AI_TOOLS.route) {
            AIToolScreen(navController, viewModel)
        }
        composable(
            Routes.AI_RESULT_SCREEN.route
        ) {
            AIResultScreen(navController, viewModel)
        }
        composable(
            Routes.EDIT_SCREEN.route,
        ) {
            EditScreen(navController, viewModel)
        }

        composable(
            Routes.BRUSH_SCREEN.route
        ) {
            BrushScreen(navController, viewModel)
        }

        composable(
            Routes.HAIR_SCREEN.route
        ) {
            HairScreen(navController, viewModel)
        }

        composable(
            Routes.ADD_TEXT.route
        ) {
            AddTextScreen(navController, viewModel)
        }

        composable(
            Routes.FILTER_TOOL.route
        ) {
            FilterScreen(navController, viewModel)
        }

        composable(
            Routes.PLAN.route
        ) {
            PlanScreen(navController, viewModel, billingViewModel)
        }
    }
}