package it.airbagstudio.ticare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.airbagstudio.ticare.pages.SplashPage
import it.airbagstudio.ticare.pages.login.LoginScreen
import it.airbagstudio.ticare.pages.patientDetails.PatientDetailsScreen
import it.airbagstudio.ticare.pages.patientsList.PatientListScreen
import kotlinx.coroutines.CoroutineScope


@Composable
fun EclinicNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = Destinations.SPLASH_ROUTE,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.SPLASH_ROUTE) {
            SplashPage(nav = navActions)
        }

        composable(Destinations.LOGIN_ROUTE){
            LoginScreen()
        }

        composable(Destinations.PATIENTS_LIST_ROUTE){
            PatientListScreen(navActions = navActions)
        }
        composable(Destinations.PATIENT_DETAILS_ROUTE){
            PatientDetailsScreen(navActions = navActions){
                navController.popBackStack()
            }
        }
    }
}