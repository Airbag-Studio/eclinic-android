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
import it.airbagstudio.ticare.pages.carePlans.details.CarePlanDetailsScreen
import it.airbagstudio.ticare.pages.carePlans.list.CarePlanesListScreen
import it.airbagstudio.ticare.pages.consumptions.ConsumptionListScreen
import it.airbagstudio.ticare.pages.coursesGeneric.CoursesScreen
import it.airbagstudio.ticare.pages.diary.DiaryScreen
import it.airbagstudio.ticare.pages.drugsAdministration.DrugsAdministrationScreen
import it.airbagstudio.ticare.pages.login.LoginScreen
import it.airbagstudio.ticare.pages.nursingCourses.NursingCoursesScreen
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentScreen
import it.airbagstudio.ticare.pages.patientDetails.PatientDetailsScreen
import it.airbagstudio.ticare.pages.patientDetails.alertsAllergies.AlertAllergiesScreen
import it.airbagstudio.ticare.pages.patientInfo.PatientInfoScreen
import it.airbagstudio.ticare.pages.patientsList.PatientListScreen
import it.airbagstudio.ticare.pages.settings.SettingsPage
import it.airbagstudio.ticare.pages.splashPage.SplashPageScreen
import it.airbagstudio.ticare.pages.tasksGeneric.TaskListScreen
import it.airbagstudio.ticare.pages.vitalParameters.list.VitalParametersScreen
import it.airbagstudio.ticare.pages.workinghours.list.WorkingHoursListScreen
import it.airbagstudio.ticare.pages.wounds.checks.details.CheckDetailsPage
import it.airbagstudio.ticare.pages.wounds.details.WoundDetailsScreen
import it.airbagstudio.ticare.pages.wounds.list.WoundListScreen
import kotlinx.coroutines.CoroutineScope


@Composable
fun EclinicNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.SPLASH_ROUTE,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    // val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

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
            PatientInfoScreen {
                navController.popBackStack()
            }
        }
        composable(Destinations.DRUG_ADMINISTRATION_ROUTE) {
            DrugsAdministrationScreen {
                navController.popBackStack()
            }
        }
        composable(Destinations.DRUG_ADMINISTRATION_ROUTE_NO_SHIFT) {
            DrugsAdministrationScreen {
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
        composable(Destinations.CARE_PLANS_ROUTE){
            CarePlanesListScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }

        composable(Destinations.NURSING_COURSES_ROUTE) {
            NursingCoursesScreen {
                navController.popBackStack()
            }
        }

        composable(Destinations.CARE_PLANE_DETAILS_ROUTE){
            CarePlanDetailsScreen {
                navController.popBackStack()
            }
        }

        composable(Destinations.WOUNDS_ROUTE){
            WoundListScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }

        composable(Destinations.WOUND_DETAILS_ROUTE){
            WoundDetailsScreen(navigationActions = navActions) {
                navController.popBackStack()
            }
        }

        composable(Destinations.CHECK_DETAILS_ROUTE){
            CheckDetailsPage {
                navController.popBackStack()
            }
        }

        composable(Destinations.SETTING_ROUTE){
            SettingsPage(navigationActions = navActions) {
                navController.popBackStack()
            }
        }

        composable(Destinations.CONSUMPTION_LIST_ROUTE) {
            ConsumptionListScreen {
                navController.popBackStack()
            }
        }

        composable(Destinations.WORKING_HOURS_LIST_ROUTE){
            WorkingHoursListScreen {
                navController.popBackStack()
            }
        }
        composable(Destinations.PATIENT_ALERT_ALLERGIES_ROUTE){
            AlertAllergiesScreen {
                navController.popBackStack()
            }
        }

        composable(Destinations.COURSES_ROUTE){
            CoursesScreen {
                navController.popBackStack()
            }
        }

        composable(Destinations.TASKS_ROUTE){
            TaskListScreen {
                navController.popBackStack()
            }
        }
    }
}