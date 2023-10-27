package it.airbagstudio.ticare.navigation

import androidx.navigation.NavController
import it.airbagstudio.ticare.navigation.DestinationsArgs.DATE_TIME
import it.airbagstudio.ticare.navigation.DestinationsArgs.PATIENT_COD
import it.airbagstudio.ticare.navigation.DestinationsArgs.SHIFT_END
import it.airbagstudio.ticare.navigation.DestinationsArgs.SHIFT_NAME
import it.airbagstudio.ticare.navigation.DestinationsArgs.SHIFT_START
import it.airbagstudio.ticare.navigation.Screens.ALLERGIES_SCREEN
import it.airbagstudio.ticare.navigation.Screens.CARE_PLANS_SCREEN
import it.airbagstudio.ticare.navigation.Screens.DIARY_SCREEN
import it.airbagstudio.ticare.navigation.Screens.DRUG_ADMINISTRATION_SCREEN
import it.airbagstudio.ticare.navigation.Screens.LOGIN_SCREEN
import it.airbagstudio.ticare.navigation.Screens.NURSING_COURSES_SCREEN
import it.airbagstudio.ticare.navigation.Screens.OTHER_SERVICE_SCREEN
import it.airbagstudio.ticare.navigation.Screens.PATIENTS_LIST_SCREEN
import it.airbagstudio.ticare.navigation.Screens.PATIENT_DETAILS_SCREEN
import it.airbagstudio.ticare.navigation.Screens.PATIENT_INFO_SCREEN
import it.airbagstudio.ticare.navigation.Screens.SPLASH_SCREEN
import it.airbagstudio.ticare.navigation.Screens.VITAL_PARAMETERS_SCREEN

private object Screens{
    const val SPLASH_SCREEN = "splashScreen"
    const val LOGIN_SCREEN = "loginScreen"
    const val PATIENTS_LIST_SCREEN = "patientsListScreen"
    const val PATIENT_DETAILS_SCREEN = "patientDetailsScreen"
    const val PATIENT_INFO_SCREEN = "patientInfoScreen"
    const val DRUG_ADMINISTRATION_SCREEN = "drugAdministrationScreen"
    const val ALLERGIES_SCREEN = "allergiesScreen"
    const val OTHER_SERVICE_SCREEN = "otherServiceScreen"
    const val VITAL_PARAMETERS_SCREEN = "vitalSignsScreen"
    const val DIARY_SCREEN = "diaryScreen"
    const val CARE_PLANS_SCREEN = "carePlansScreen"
    const val NURSING_COURSES_SCREEN = "nursingCoursesScreen"
}

object DestinationsArgs{
    const val PATIENT_COD = "patientCod"
    const val SHIFT_START = "shiftStart"
    const val SHIFT_END = "shiftEnd"
    const val SHIFT_NAME = "shiftName"
    const val DATE_TIME = "dateTime"
    const val NOTE_CONTENT = "noteContent"
}

object Destinations{
    const val SPLASH_ROUTE = SPLASH_SCREEN
    const val LOGIN_ROUTE = LOGIN_SCREEN
    const val PATIENTS_LIST_ROUTE = PATIENTS_LIST_SCREEN
    const val PATIENT_DETAILS_ROUTE = "$PATIENT_DETAILS_SCREEN/{$PATIENT_COD}"
    const val PATIENT_INFO_ROUTE = "$PATIENT_INFO_SCREEN/{$PATIENT_COD}"
    const val DRUG_ADMINISTRATION_ROUTE = "$DRUG_ADMINISTRATION_SCREEN/{$PATIENT_COD}/{$DATE_TIME}/{${SHIFT_START}}/{$SHIFT_END}/{$SHIFT_NAME}"
    const val DRUG_ADMINISTRATION_ROUTE_NO_SHIFT = "$DRUG_ADMINISTRATION_SCREEN/{$PATIENT_COD}/{$DATE_TIME}/{$SHIFT_NAME}"
    const val ALLERGIES_ROUTE = "$ALLERGIES_SCREEN/{$PATIENT_COD}"
    const val OTHER_SERVICE_ROUTE = "$OTHER_SERVICE_SCREEN/{$PATIENT_COD}"
    const val VITAL_PARAMETERS_ROUTE = "$VITAL_PARAMETERS_SCREEN/{$PATIENT_COD}"
    const val DIARY_ROUTE = "$DIARY_SCREEN/{$PATIENT_COD}"
    const val CARE_PLANS_ROUTE = "$CARE_PLANS_SCREEN/{$PATIENT_COD}"
    const val NURSING_COURSES_ROUTE = "$NURSING_COURSES_SCREEN/{$PATIENT_COD}/{$DATE_TIME}/{${SHIFT_START}}/{$SHIFT_END}/{$SHIFT_NAME}"
    const val NURSING_COURSES_ROUTE_NO_SHIFT = "$NURSING_COURSES_SCREEN/{$PATIENT_COD}/{$DATE_TIME}/{$SHIFT_NAME}"

}

class NavigationActions(private val navController: NavController){

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

    fun navigateToDrugAdministration(patientCod: String,dateTime:Long,shiftStart:String,shiftEnd:String, shiftName:String){
        navController.navigate("$DRUG_ADMINISTRATION_SCREEN/$patientCod/$dateTime/$shiftStart/$shiftEnd/$shiftName")
    }

    fun navigateToDrugAdministration(patientCod: String,dateTime:Long, shiftName:String){
        navController.navigate("$DRUG_ADMINISTRATION_SCREEN/$patientCod/$dateTime/$shiftName")
    }

    fun navigateToAllergies(patientCod: String){
        navController.navigate("$ALLERGIES_SCREEN/$patientCod")
    }

    fun navigateToOtherServices(patientCod: String){
        navController.navigate("$OTHER_SERVICE_SCREEN/$patientCod")
    }

    fun navigateToVitalParameters(patientCod: String){
        navController.navigate("$VITAL_PARAMETERS_SCREEN/$patientCod")
    }

    fun navigateToDiary(patientCod: String){
        navController.navigate("$DIARY_SCREEN/$patientCod")
    }

    fun navigateToCarePlans(patientCod: String){
        navController.navigate("$CARE_PLANS_SCREEN/$patientCod")
    }

    fun navigateToNursingCourses(patientCod: String,dateTime:Long,shiftStart:String,shiftEnd:String, shiftName:String){
        navController.navigate("$NURSING_COURSES_SCREEN/$patientCod/$dateTime/$shiftStart/$shiftEnd/$shiftName")
    }

    fun navigateToNursingCourses(patientCod: String,dateTime:Long, shiftName:String){
        navController.navigate("$NURSING_COURSES_SCREEN/$patientCod/$dateTime/$shiftName")
    }


}