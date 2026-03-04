package it.airbagstudio.ticare.navigation

import androidx.navigation.NavController
import it.airbagstudio.ticare.navigation.DestinationsArgs.CHECK_ID
import it.airbagstudio.ticare.navigation.DestinationsArgs.COURSE_TYPE
import it.airbagstudio.ticare.navigation.DestinationsArgs.DATE_TIME
import it.airbagstudio.ticare.navigation.DestinationsArgs.GENDER_ID
import it.airbagstudio.ticare.navigation.DestinationsArgs.ID
import it.airbagstudio.ticare.navigation.DestinationsArgs.PATIENT_COD
import it.airbagstudio.ticare.navigation.DestinationsArgs.SHIFT_END
import it.airbagstudio.ticare.navigation.DestinationsArgs.SHIFT_ID
import it.airbagstudio.ticare.navigation.DestinationsArgs.SHIFT_NAME
import it.airbagstudio.ticare.navigation.DestinationsArgs.SHIFT_START
import it.airbagstudio.ticare.navigation.DestinationsArgs.TASK_TYPE
import it.airbagstudio.ticare.navigation.ScreensKeys.CARE_PLANS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.CARE_PLAN_DETAILS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.CHECK_DETAILS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.CONSUMPTION_LIST_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.COURSES_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.DIARY_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.DRUG_ADMINISTRATION_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.LOGIN_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.MEDICAL_DIAGNOSES_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.NURSING_COURSES_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.OTHER_SERVICE_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.PATIENTS_LIST_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.PATIENT_ALERT_ALLERGIES_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.PATIENT_DETAILS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.PATIENT_INFO_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.SETTINGS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.SPLASH_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.TASKS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.VITAL_PARAMETERS_CHARTS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.VITAL_PARAMETERS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.WORKING_HOURS_LIST_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.WOUNDS_SCREEN
import it.airbagstudio.ticare.navigation.ScreensKeys.WOUND_DETAILS_SCREEN
import it.airbagstudio.ticare.pages.patientDetails.form.ui.navigation.AppDestinations

object ScreensKeys{
    const val SPLASH_SCREEN = "splashScreen"
    const val LOGIN_SCREEN = "loginScreen"
    const val PATIENTS_LIST_SCREEN = "patientsListScreen"
    const val PATIENT_DETAILS_SCREEN = "patientDetailsScreen"
    const val PATIENT_ALERT_ALLERGIES_SCREEN = "patientAlertAllergiesScreen"
    const val PATIENT_INFO_SCREEN = "patientInfoScreen"
    const val DRUG_ADMINISTRATION_SCREEN = "drugAdministrationScreen"
    const val OTHER_SERVICE_SCREEN = "otherServiceScreen"
    const val VITAL_PARAMETERS_SCREEN = "vitalSignsScreen"
    const val DIARY_SCREEN = "diaryScreen"
    const val CARE_PLANS_SCREEN = "carePlansScreen"
    const val CARE_PLAN_DETAILS_SCREEN = "carePlanDetailsScreen"
    const val NURSING_COURSES_SCREEN = "nursingCoursesScreen"
    const val WOUNDS_SCREEN = "woundsScreen"
    const val WOUND_DETAILS_SCREEN = "woundDetailsScreen"
    const val CHECK_DETAILS_SCREEN = "checkDetailsScreen"
    const val SETTINGS_SCREEN = "settingsScreen"
    const val CONSUMPTION_LIST_SCREEN = "consumptionListScreen"
    const val WORKING_HOURS_LIST_SCREEN = "workingHoursListScreen"
    const val COURSES_SCREEN = "coursesScreen"
    const val TASKS_SCREEN = "tasksScreen"
    const val VITAL_PARAMETERS_CHARTS_SCREEN = "vitalSignsChartsScreen"

    const val MEDICAL_DIAGNOSES_SCREEN = "medicalDiagnosesScreen"
}

object DestinationsArgs{
    const val COURSE_TYPE: String = "courseType"
    const val TASK_TYPE: String = "taskType"
    const val CHECK_ID: String = "checkId"
    const val ID: String = "id"
    const val PATIENT_COD = "patientCod"
    const val SHIFT_START = "shiftStart"
    const val SHIFT_END = "shiftEnd"
    const val SHIFT_NAME = "shiftName"
    const val DATE_TIME = "dateTime"
    const val NOTE_CONTENT = "noteContent"
    const val GENDER_ID = "genderId"
    const val SHIFT_ID = "shiftId"
}

object Destinations{
    const val SPLASH_ROUTE = SPLASH_SCREEN
    const val LOGIN_ROUTE = LOGIN_SCREEN
    const val PATIENTS_LIST_ROUTE = PATIENTS_LIST_SCREEN
    const val PATIENT_DETAILS_ROUTE = "$PATIENT_DETAILS_SCREEN/{$PATIENT_COD}"
    const val PATIENT_INFO_ROUTE = "$PATIENT_INFO_SCREEN/{$PATIENT_COD}"
    const val DRUG_ADMINISTRATION_ROUTE = "$DRUG_ADMINISTRATION_SCREEN/{$PATIENT_COD}/{$DATE_TIME}/{${SHIFT_START}}/{$SHIFT_END}/{$SHIFT_NAME}"
    const val DRUG_ADMINISTRATION_ROUTE_NO_SHIFT = "$DRUG_ADMINISTRATION_SCREEN/{$PATIENT_COD}/{$DATE_TIME}/{$SHIFT_NAME}"
    const val PATIENT_ALERT_ALLERGIES_ROUTE = "$PATIENT_ALERT_ALLERGIES_SCREEN/{$PATIENT_COD}"
    const val OTHER_SERVICE_ROUTE = "$OTHER_SERVICE_SCREEN/{$PATIENT_COD}"
    const val VITAL_PARAMETERS_ROUTE = "$VITAL_PARAMETERS_SCREEN/{$PATIENT_COD}"
    const val DIARY_ROUTE = "$DIARY_SCREEN/{$PATIENT_COD}"
    const val CARE_PLANS_ROUTE = "$CARE_PLANS_SCREEN/{$PATIENT_COD}"
    const val NURSING_COURSES_ROUTE = "$NURSING_COURSES_SCREEN/{$PATIENT_COD}/{$COURSE_TYPE}"
    const val CARE_PLANE_DETAILS_ROUTE = "$CARE_PLAN_DETAILS_SCREEN/{$PATIENT_COD}/{$ID}"
    const val WOUNDS_ROUTE = "$WOUNDS_SCREEN/{$PATIENT_COD}"
    const val WOUND_DETAILS_ROUTE = "$WOUND_DETAILS_SCREEN/{$PATIENT_COD}/{$ID}/{$GENDER_ID}"
    const val CHECK_DETAILS_ROUTE = "$CHECK_DETAILS_SCREEN/{$PATIENT_COD}/{$ID}/{$CHECK_ID}"
    const val SETTING_ROUTE = SETTINGS_SCREEN
    const val CONSUMPTION_LIST_ROUTE = CONSUMPTION_LIST_SCREEN
    const val WORKING_HOURS_LIST_ROUTE = WORKING_HOURS_LIST_SCREEN
    const val COURSES_ROUTE = "$COURSES_SCREEN/{$PATIENT_COD}/{$COURSE_TYPE}"
    const val TASKS_ROUTE = "$TASKS_SCREEN/{$PATIENT_COD}/{$TASK_TYPE}/{$SHIFT_ID}"
    const val VITAL_PARAMETERS_CHARTS_ROUTE = "$VITAL_PARAMETERS_CHARTS_SCREEN/{$PATIENT_COD}"

    const val MEDICAL_DIAGNOSES_ROUTE = "$MEDICAL_DIAGNOSES_SCREEN/{$PATIENT_COD}"
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

    fun navigateToOtherServices(patientCod: String){
        navController.navigate("$OTHER_SERVICE_SCREEN/$patientCod")
    }

    fun navigateToVitalParameters(patientCod: String){
        navController.navigate("$VITAL_PARAMETERS_SCREEN/$patientCod")
    }

    fun navigateToVitalParametersCharts(patientCod: String) {
        navController.navigate("$VITAL_PARAMETERS_CHARTS_SCREEN/$patientCod")
    }

    fun navigateToDiary(patientCod: String){
        navController.navigate("$DIARY_SCREEN/$patientCod")
    }

    fun navigateToCarePlans(patientCod: String){
        navController.navigate("$CARE_PLANS_SCREEN/$patientCod")
    }

    fun navigateToNursingCourses(patientCod: String, courseType: String){
        navController.navigate("$NURSING_COURSES_SCREEN/$patientCod/$courseType")
    }

    fun navigateToCarePlanDetailsScreen(patientCod: String,id: String){
        navController.navigate("$CARE_PLAN_DETAILS_SCREEN/$patientCod/$id")
    }

    fun navigateToWounds(patientCod: String){
        navController.navigate("$WOUNDS_SCREEN/$patientCod")
    }

    fun navigateToAlertAndAllergies(patientCod: String){
        navController.navigate("$PATIENT_ALERT_ALLERGIES_SCREEN/$patientCod")
    }

    fun navigateToWoundDetails(patientCod: String,id: Int, genderId: Int){
        navController.navigate("$WOUND_DETAILS_SCREEN/$patientCod/$id/$genderId")
    }

    fun navigateToCheckDetails(patientCod: String,woundId: String,checkId: Int){
        navController.navigate("$CHECK_DETAILS_SCREEN/$patientCod/$woundId/$checkId")
    }

    fun navigateToSettings(){
        navController.navigate(SETTINGS_SCREEN)
    }

    fun navigateToConsumptionList() {
        navController.navigate(CONSUMPTION_LIST_SCREEN)
    }

    fun navigateToWorkingHours(){
        navController.navigate(WORKING_HOURS_LIST_SCREEN)
    }

    fun navigateToCourses(patientCod: String,courseType: String){
        navController.navigate("$COURSES_SCREEN/$patientCod/$courseType")
    }
    fun navigateToTasksScreen(patientCod: String,taskType: String, taskId: Int = -1){
        navController.navigate("$TASKS_SCREEN/$patientCod/$taskType/$taskId")
    }

    fun navigateToFormsHome() {
        navController.navigate(AppDestinations.HOME_ROUTE)
    }

    fun navigateToMedicalDiagnoses(patientCod: String) {
        navController.navigate("$MEDICAL_DIAGNOSES_SCREEN/$patientCod")
    }
}