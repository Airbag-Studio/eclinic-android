package it.airbagstudio.ticare.navigation

import androidx.activity.compose.BackHandler
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
import it.airbagstudio.ticare.LoginRedirect
import it.airbagstudio.ticare.pages.allergies.AllergiesScreen
import it.airbagstudio.ticare.pages.diary.DiaryScreen
import it.airbagstudio.ticare.pages.drugsAdministration.DrugsAdministrationScreen
import it.airbagstudio.ticare.pages.login.LoginScreen
import it.airbagstudio.ticare.pages.nursingCourses.NursingCoursesScreen
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentScreen
import it.airbagstudio.ticare.pages.patientDetails.PatientDetailsScreen
import it.airbagstudio.ticare.pages.patientInfo.PatientInfoScreen
import it.airbagstudio.ticare.pages.patientsList.PatientListScreen
import it.airbagstudio.ticare.pages.splashPage.SplashPageScreen
import it.airbagstudio.ticare.pages.vitalParameters.list.VitalParametersScreen
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

    LoginRedirect.onCredentialRefresh = {
        navActions.navigateToLogin()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.SPLASH_ROUTE) {
            BackHandler(true) {

            }
            SplashPageScreen(nav = navActions)
        }

        composable(Destinations.LOGIN_ROUTE) {
            BackHandler(true) {

            }
            LoginScreen(navigationActions = navActions)
        }

        composable(Destinations.PATIENTS_LIST_ROUTE) {
            BackHandler(true) {

            }
            PatientListScreen(navActions = navActions)
        }
        composable(Destinations.PATIENT_DETAILS_ROUTE) {
            PatientDetailsScreen(navActions = navActions) {
                navController.popBackStack()
            }
        }
        composable(Destinations.PATIENT_INFO_ROUTE) {
            PatientInfoScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }
        composable(Destinations.DRUG_ADMINISTRATION_ROUTE) {
            DrugsAdministrationScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }
        composable(Destinations.DRUG_ADMINISTRATION_ROUTE_NO_SHIFT) {
            DrugsAdministrationScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }
        composable(Destinations.ALLERGIES_ROUTE) {
            AllergiesScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }
        composable(Destinations.OTHER_SERVICE_ROUTE){
            OtherTreatmentScreen {
                navController.popBackStack()
            }
        }
        composable(Destinations.VITAL_PARAMETERS_ROUTE){
            VitalParametersScreen {
                navController.popBackStack()
            }
        }
        composable(Destinations.DIARY_ROUTE){
            DiaryScreen {
                navController.popBackStack()
            }
        }

        composable(Destinations.NURSING_COURSES_ROUTE) {
            NursingCoursesScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }

        composable(Destinations.NURSING_COURSES_ROUTE_NO_SHIFT) {
            NursingCoursesScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }
    }
}