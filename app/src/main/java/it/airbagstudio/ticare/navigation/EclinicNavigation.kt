package it.airbagstudio.ticare.navigation

import androidx.navigation.NavController
import it.airbagstudio.ticare.navigation.DestinationsArgs.PATIENT_COD
import it.airbagstudio.ticare.navigation.Screens.LOGIN_SCREEN
import it.airbagstudio.ticare.navigation.Screens.PATIENTS_LIST_SCREEN
import it.airbagstudio.ticare.navigation.Screens.PATIENT_DETAILS_SCREEN
import it.airbagstudio.ticare.navigation.Screens.PATIENT_INFO_SCREEN
import it.airbagstudio.ticare.navigation.Screens.SPLASH_SCREEN

private object Screens{
    const val SPLASH_SCREEN = "splashScreen"
    const val LOGIN_SCREEN = "loginScreen"
    const val PATIENTS_LIST_SCREEN = "patientsListScreen"
    const val PATIENT_DETAILS_SCREEN = "patientDetailsScreen"
    const val PATIENT_INFO_SCREEN = "patientInfoScreen"
}

object DestinationsArgs{
    const val PATIENT_COD = "patientCod"
}

object Destinations{
    const val SPLASH_ROUTE = SPLASH_SCREEN
    const val LOGIN_ROUTE = LOGIN_SCREEN
    const val PATIENTS_LIST_ROUTE = PATIENTS_LIST_SCREEN
    const val PATIENT_DETAILS_ROUTE = "$PATIENT_DETAILS_SCREEN/{$PATIENT_COD}"
    const val PATIENT_INFO_ROUTE = "$PATIENT_INFO_SCREEN/{$PATIENT_COD}"
}

class NavigationActions(private val navController: NavController){

    fun navigateToSplash(){
        navController.navigate(SPLASH_SCREEN)
    }

    fun navigateToLogin(){
        navController.navigate(LOGIN_SCREEN)
    }

    fun navigateToPatientsList(){
        navController.navigate(PATIENTS_LIST_SCREEN)
    }

    fun navigateToPatientDetails(patientCod: String){
        navController.navigate("$PATIENT_DETAILS_SCREEN/$patientCod")
    }

    fun navigateToPatientInfo(patientCod: String) {
        navController.navigate("$PATIENT_INFO_SCREEN/$patientCod")
    }
}