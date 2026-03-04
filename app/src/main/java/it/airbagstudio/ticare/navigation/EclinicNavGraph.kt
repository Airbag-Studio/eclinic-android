package it.airbagstudio.ticare.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ch.ticare.eclinic.library.repository.UserDetailRepository
import it.airbagstudio.ticare.LoginRedirect
import it.airbagstudio.ticare.pages.carePlans.details.CarePlanDetailsScreen
import it.airbagstudio.ticare.pages.carePlans.list.CarePlanesListScreen
import it.airbagstudio.ticare.pages.consumptions.ConsumptionListScreen
import it.airbagstudio.ticare.pages.coursesGeneric.CoursesScreen
import it.airbagstudio.ticare.pages.diary.DiaryScreen
import it.airbagstudio.ticare.pages.drugsAdministration.DrugsAdministrationScreen
import it.airbagstudio.ticare.pages.login.LoginScreen
import it.airbagstudio.ticare.pages.medicalDiagnoses.MedicalDiagnosesView
import it.airbagstudio.ticare.pages.nursingCourses.NursingCoursesScreen
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentScreen
import it.airbagstudio.ticare.pages.patientDetails.PatientDetailsScreen
import it.airbagstudio.ticare.pages.patientDetails.alertsAllergies.AlertAllergiesScreen
import it.airbagstudio.ticare.pages.patientDetails.form.di.provideFormRepository
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cam.CAMFormScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi.CbiFormScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.comid.ComidFormScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.idpall.IDPallFormScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.ipos.IPOSFormScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.pacic.PACICFormScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.seniorsitting.SeniorSittingFormScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.home.FormType
import it.airbagstudio.ticare.pages.patientDetails.form.ui.home.HomeScreen
import it.airbagstudio.ticare.pages.patientDetails.form.ui.navigation.AppDestinations
import it.airbagstudio.ticare.pages.patientDetails.form.ui.navigation.AppDestinations.CAM_FORM_ID_ARG
import it.airbagstudio.ticare.pages.patientDetails.form.ui.navigation.AppDestinations.IDPALL_FORM_ID_ARG
import it.airbagstudio.ticare.pages.patientDetails.form.ui.navigation.AppDestinations.IDPALL_FORM_ROUTE
import it.airbagstudio.ticare.pages.patientDetails.form.ui.navigation.AppDestinations.PACIC_FORM_ID_ARG
import it.airbagstudio.ticare.pages.patientInfo.PatientInfoScreen
import it.airbagstudio.ticare.pages.patientsList.PatientListScreen
import it.airbagstudio.ticare.pages.settings.SettingsPage
import it.airbagstudio.ticare.pages.splashPage.SplashPageScreen
import it.airbagstudio.ticare.pages.tasksGeneric.TaskListScreen
import it.airbagstudio.ticare.pages.vitalParameters.charts.VitalParamsChartsView
import it.airbagstudio.ticare.pages.vitalParameters.list.VitalParametersScreen
import it.airbagstudio.ticare.pages.workinghours.list.WorkingHoursListScreen
import it.airbagstudio.ticare.pages.wounds.checks.details.CheckDetailsPage
import it.airbagstudio.ticare.pages.wounds.details.WoundDetailsScreen
import it.airbagstudio.ticare.pages.wounds.list.WoundListScreen


@Composable
fun EclinicNavGraph(
    userDetailRepository: UserDetailRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.SPLASH_ROUTE,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
    val formRepository = provideFormRepository(context = LocalContext.current, userDetailRepository = userDetailRepository)

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
            VitalParametersScreen(
                navigationActions = navActions
            ) {
                navController.popBackStack()
            }
        }
        composable(Destinations.VITAL_PARAMETERS_CHARTS_ROUTE){
            VitalParamsChartsView {
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

        composable(Destinations.MEDICAL_DIAGNOSES_ROUTE){
            MedicalDiagnosesView {
                navController.popBackStack()
            }
        }



        composable(
            route = AppDestinations.HOME_ROUTE,
            arguments = listOf(navArgument(AppDestinations.HOME_ROUTE_SAVED_ARG) {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val saved = backStackEntry.arguments?.getBoolean(AppDestinations.HOME_ROUTE_SAVED_ARG) ?: false
            HomeScreen(
                showSnackbarOnEntry = saved, // Pass the saved flag
                onNavigateToForm = { formTypeEnum, formId -> // Changed to formTypeEnum
                    // Use typeName for comparison or switch on enum
                    when (formTypeEnum) {
                        FormType.CBI -> {
                            navController.navigate(AppDestinations.cbiFormRoute(formId))
                        }

                        FormType.COMID -> {
                            navController.navigate(AppDestinations.comidFormRoute(formId))
                        }

                        FormType.IPOS -> { // Unified IPOS form
                            navController.navigate(AppDestinations.iposFormRoute(formId))
                        }

                        FormType.SENIOR_SITTING -> { // Unified Senior Sitting form
                            navController.navigate(AppDestinations.seniorSittingFormRoute(formId))
                        }

                        FormType.IDPALL -> {
                            navController.navigate(AppDestinations.idpallFormRoute(formId))
                        }

                        FormType.CAM -> {
                            navController.navigate(AppDestinations.camFormRoute(formId))
                        }

                        FormType.PACIC_S ->
                            navController.navigate(AppDestinations.pacicFormRoute(formId))

                    }
                },
                onBack = {
                    navController.popBackStack(Destinations.PATIENT_DETAILS_ROUTE, inclusive = false)
                }
            )
        }

        composable(
            route = AppDestinations.CBI_FORM_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.CBI_FORM_ID_ARG) { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, delayMillis = 0)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
            // Consider adding popEnterTransition and popExitTransition for a complete animation set
            // popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            // popExitTransition = { slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(300)) }
        ) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString(AppDestinations.CBI_FORM_ID_ARG)
            val actualFormId = if (formId == "new") null else formId

            CbiFormScreen(
                formId = actualFormId,
                onClose = {
                    navController.popBackStack(AppDestinations.HOME_ROUTE, inclusive = false)
                },
                onSaved = {
                    // Navigate to home indicating a save occurred
                    navController.navigate(AppDestinations.homeRoute(saved = true)) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(AppDestinations.homeRoute(saved = false)) { // Use the route pattern
                            inclusive = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppDestinations.COMID_FORM_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.COMID_FORM_ID_ARG) { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, delayMillis = 0)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
        ) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString(AppDestinations.COMID_FORM_ID_ARG)
            val actualFormId = if (formId == "new") null else formId

            ComidFormScreen(
                formId = actualFormId,
                onClose = {
                    navController.popBackStack(AppDestinations.HOME_ROUTE, inclusive = false)
                },
                onSaved = {
                    navController.navigate(AppDestinations.homeRoute(saved = true)) {
                        popUpTo(AppDestinations.homeRoute(saved = false)) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppDestinations.IPOS_FORM_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.IPOS_FORM_ID_ARG) { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, delayMillis = 0)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
        ) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString(AppDestinations.IPOS_FORM_ID_ARG)
            val actualFormId = if (formId == "new") null else formId

            IPOSFormScreen(
                formId = actualFormId,
                onClose = {
                    navController.popBackStack(AppDestinations.HOME_ROUTE, inclusive = false)
                },
                onSaved = { savedFormId ->
                    navController.navigate(AppDestinations.homeRoute(saved = true)) {
                        popUpTo(AppDestinations.homeRoute(saved = false)) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppDestinations.SENIOR_SITTING_FORM_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.SENIOR_SITTING_FORM_ID_ARG) { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, delayMillis = 0)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
        ) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString(AppDestinations.SENIOR_SITTING_FORM_ID_ARG)
            val actualFormId = if (formId == "new") null else formId

            SeniorSittingFormScreen(
                formId = actualFormId,
                onClose = {
                    navController.popBackStack(AppDestinations.HOME_ROUTE, inclusive = false)
                },
                onSaved = { savedFormId ->
                    navController.navigate(AppDestinations.homeRoute(saved = true)) {
                        popUpTo(AppDestinations.homeRoute(saved = false)) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = IDPALL_FORM_ROUTE,
            arguments = listOf(
                navArgument(IDPALL_FORM_ID_ARG) { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, delayMillis = 0)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
        ) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString(IDPALL_FORM_ID_ARG)
            val actualFormId = if (formId == "new") null else formId

            // Corrected call to IDPallFormScreen
            IDPallFormScreen(
                formId = actualFormId,
                onNavigateBack = {
                    navController.popBackStack(AppDestinations.HOME_ROUTE, inclusive = false)

                }
            )
        }


        composable( // Added CAM Form
            route = AppDestinations.CAM_FORM_ROUTE,
            arguments = listOf(
                navArgument(CAM_FORM_ID_ARG) { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, delayMillis = 0)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
        ) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString(CAM_FORM_ID_ARG)
            val actualFormId = if (formId == "new") null else formId

            CAMFormScreen(
                formId = actualFormId,
                onClose = {
                    navController.popBackStack(AppDestinations.HOME_ROUTE, inclusive = false)

                },
                onSaved = {
                    navController.navigate(AppDestinations.homeRoute(saved = true)) {
                        popUpTo(AppDestinations.homeRoute(saved = false)) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable( // Added CAM Form
            route = AppDestinations.PACIC_FORM_ROUTE,
            arguments = listOf(
                navArgument(PACIC_FORM_ID_ARG) { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, delayMillis = 0)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
        ) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString(PACIC_FORM_ID_ARG)
            val actualFormId = if (formId == "new") null else formId

            PACICFormScreen(
                formId = actualFormId,
                onClose = {
                    navController.popBackStack(AppDestinations.HOME_ROUTE, inclusive = false)

                },
                onSaved = {
                    navController.navigate(AppDestinations.homeRoute(saved = true)) {
                        popUpTo(AppDestinations.homeRoute(saved = false)) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}