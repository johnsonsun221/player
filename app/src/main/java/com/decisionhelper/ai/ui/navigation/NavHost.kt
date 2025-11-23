package com.decisionhelper.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.decisionhelper.ai.ui.screens.history.HistoryScreen
import com.decisionhelper.ai.ui.screens.home.HomeScreen
import com.decisionhelper.ai.ui.screens.options.OptionsScreen
import com.decisionhelper.ai.ui.screens.result.ResultScreen

/**
 * 导航路由定义
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Options : Screen("options/{decisionId}") {
        fun createRoute(decisionId: Long) = "options/$decisionId"
    }
    data object Result : Screen("result/{decisionId}") {
        fun createRoute(decisionId: Long) = "result/$decisionId"
    }
    data object History : Screen("history")
}

/**
 * 应用导航主机
 */
@Composable
fun DecisionHelperNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // 首页 - 问题输入
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToOptions = { decisionId ->
                    navController.navigate(Screen.Options.createRoute(decisionId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        // 选项列表页
        composable(
            route = Screen.Options.route,
            arguments = listOf(
                navArgument("decisionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val decisionId = backStackEntry.arguments?.getLong("decisionId") ?: 0L
            OptionsScreen(
                decisionId = decisionId,
                onNavigateToResult = { id ->
                    navController.navigate(Screen.Result.createRoute(id))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 结果展示页
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("decisionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val decisionId = backStackEntry.arguments?.getLong("decisionId") ?: 0L
            ResultScreen(
                decisionId = decisionId,
                onNavigateHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 历史记录页
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResult = { decisionId ->
                    navController.navigate(Screen.Result.createRoute(decisionId))
                }
            )
        }
    }
}
