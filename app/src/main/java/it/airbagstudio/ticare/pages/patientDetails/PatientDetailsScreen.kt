package it.airbagstudio.ticare.pages.patientDetails

import android.net.Uri
import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ch.ticare.eclinic.library.entity.CaseContacts
import ch.ticare.eclinic.library.entity.CaseDetail
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.Gender
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.entity.ToolTag
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.DropDownButton
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.HeaderIconText
import it.airbagstudio.ticare.ui.components.ImageRequestData
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.components.PatientImage
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.md_theme_dark_secondaryContainer
import it.airbagstudio.ticare.utils.getIconId
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    viewModel: PatientDetailsScreenViewModel = hiltViewModel(),
    navActions: NavigationActions,
    onBack: () -> Unit
) {

    LifecycleResumeEffect(Unit) {
        // Do something on resume or launch effect
        viewModel.downloadData()
        onPauseOrDispose {

        }
    }
    var showShiftsPopup by remember { mutableStateOf(false) }

    var showDatePicker by remember {
        mutableStateOf(false)
    }
    val calendar = Calendar.getInstance()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val tools by viewModel.tools.collectAsStateWithLifecycle()

    val menuItems = listOf(
        SectionListItem(
            R.string.vital_parameters,
            R.drawable.ic_vital_parameters,
            viewModel.badges.firstOrNull { it.vitalSign != null && it.vitalSign!!.badgeNumber > 0 }?.vitalSign?.badgeNumber
                ?: 0,
            ToolTag.VitalSignTask
        ) {
            navActions.navigateToVitalParameters(Uri.encode(viewModel.patientCod))
        },
        SectionListItem(
            R.string.drug_administration,
            R.drawable.ic_pills,
            viewModel.badges.firstOrNull { it.pharmacological != null && it.pharmacological!!.badgeNumber > 0 }?.pharmacological?.badgeNumber
                ?: 0,
            ToolTag.PharmacologicalTask
        ) {
            viewModel.selectedShift?.let { shift ->
                navActions.navigateToDrugAdministration(
                    Uri.encode(viewModel.patientCod),
                    viewModel.selectedDate,
                    shift.startTime,
                    shift.stopTime,
                    shiftName = shift.name
                )
            } ?: run {
                navActions.navigateToDrugAdministration(
                    Uri.encode(viewModel.patientCod),
                    viewModel.selectedDate,
                    shiftName = context.getString(R.string.all)
                )
            }
        },
        SectionListItem(
            R.string.nursing_courses,
            R.drawable.ic_nursing_courses,
            toolTag = ToolTag.NursingCourse
        ) {
            navActions.navigateToNursingCourses(
                Uri.encode(viewModel.patientCod),
                viewModel.selectedDate,
                shiftName = context.getString(R.string.all)
            )
        },
        SectionListItem(
            R.string.wounds,
            R.drawable.ic_wounds,
            toolTag = ToolTag.Wounds
        ) {
            navActions.navigateToWounds(Uri.encode(viewModel.patientCod))
        },
        SectionListItem(
            R.string.care_planes,
            R.drawable.ic_care_planes,
            viewModel.badges.firstOrNull { it.carePlan != null && it.carePlan!!.badgeNumber > 0 }?.carePlan?.badgeNumber
                ?: 0,
            toolTag = ToolTag.CarePlan
        ) {
            navActions.navigateToCarePlans(Uri.encode(viewModel.patientCod))
        },
        SectionListItem(
            R.string.nursing_courses,
            R.drawable.ic_home_care_course,
            toolTag = ToolTag.HomeCareCourse
        ) {
            navActions.navigateToNursingCourses(
                Uri.encode(viewModel.patientCod),
                viewModel.selectedDate,
                shiftName = context.getString(R.string.all)
            )
        },
        SectionListItem(
            R.string.other_prescriptions,
            R.drawable.ic_other_prescriptions,
            toolTag = ToolTag.OtherServices
        ) {
            navActions.navigateToOtherServices(Uri.encode(viewModel.patientCod))
        },
        SectionListItem(
            R.string.ergotherapy_course,
            R.drawable.ic_ergotherapy_course,
            toolTag = ToolTag.ErgotherapyCourse
        ) {
            navActions.navigateToCourses(Uri.encode(viewModel.patientCod),ToolTag.ErgotherapyCourse.name)
        },
        SectionListItem(
            R.string.atelier_course,
            R.drawable.ic_atelier_course,
            toolTag = ToolTag.AtelierCourse
        ) {
            navActions.navigateToCourses(Uri.encode(viewModel.patientCod),ToolTag.AtelierCourse.name)
        },
        SectionListItem(
            R.string.activator_course,
            R.drawable.ic_activator_course,
            toolTag = ToolTag.ActivatorCourse
        ) {
            navActions.navigateToCourses(Uri.encode(viewModel.patientCod),ToolTag.ActivatorCourse.name)
        },
        SectionListItem(
            R.string.educator_course,
            R.drawable.ic_educator_course,
            toolTag = ToolTag.EducatorCourse
        ) {
            navActions.navigateToCourses(Uri.encode(viewModel.patientCod),ToolTag.EducatorCourse.name)
        },

        SectionListItem(
            R.string.physiotherapy_course,
            R.drawable.ic_physiotherapy_course,
            toolTag = ToolTag.PhysiotherapyCourse
        ) {
            navActions.navigateToCourses(Uri.encode(viewModel.patientCod),ToolTag.PhysiotherapyCourse.name)
        },
        SectionListItem(
            R.string.nursing_task,
            R.drawable.ic_nursing_task,
            viewModel.badges.firstOrNull { it.nursing != null && it.nursing!!.badgeNumber > 0 }?.nursing?.badgeNumber
                ?: 0,
            toolTag = ToolTag.NursingTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.NursingTask.name,
                    shiftId
                )
            } ?: run {
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.NursingTask.name
                )
            }
        },
        SectionListItem(
            R.string.educator_task,
            R.drawable.ic_educator_task,
            viewModel.badges.firstOrNull { it.educator != null && it.educator!!.badgeNumber > 0 }?.educator?.badgeNumber
                ?: 0,
            toolTag = ToolTag.EducatorTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.EducatorTask.name,
                    shiftId
                )
            } ?: run {
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.EducatorTask.name
                )
            }
        },
        SectionListItem(
            R.string.physiotherapy_task,
            R.drawable.ic_physiotherapy_task,
            viewModel.badges.firstOrNull { it.physiotherapy != null && it.physiotherapy!!.badgeNumber > 0 }?.physiotherapy?.badgeNumber
                ?: 0,
            toolTag = ToolTag.PhysiotherapyTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(Uri.encode(viewModel.patientCod),ToolTag.PhysiotherapyTask.name, shiftId)
            } ?: run {
                navActions.navigateToTasksScreen(Uri.encode(viewModel.patientCod),ToolTag.PhysiotherapyTask.name)
            }
        },
        SectionListItem(
            R.string.ergotherapy_task,
            R.drawable.ic_ergotherapy_task,
            viewModel.badges.firstOrNull { it.ergotherapy != null && it.ergotherapy!!.badgeNumber > 0 }?.ergotherapy?.badgeNumber
                ?: 0,
            toolTag = ToolTag.ErgotherapyTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.ErgotherapyTask.name,
                    shiftId
                )
            } ?: run {
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.ErgotherapyTask.name
                )
            }
        },
        SectionListItem(
            R.string.atelier_task,
            R.drawable.ic_atelier_task,
            viewModel.badges.firstOrNull { it.atelier != null && it.atelier!!.badgeNumber > 0 }?.atelier?.badgeNumber
                ?: 0,
            toolTag = ToolTag.AtelierTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.AtelierTask.name,
                    shiftId
                )
            } ?: run {
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.AtelierTask.name
                )
            }
        },
        SectionListItem(
            R.string.activator_task,
            R.drawable.ic_activator_task,
            viewModel.badges.firstOrNull { it.activator != null && it.activator!!.badgeNumber > 0 }?.activator?.badgeNumber
                ?: 0,
            toolTag = ToolTag.ActivatorTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.ActivatorTask.name,
                    shiftId
                )
            } ?: run {
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.ActivatorTask.name
                )
            }
        },
        SectionListItem(
            R.string.generic_task,
            R.drawable.ic_generic_task,
            viewModel.badges.firstOrNull { it.genericService != null && it.genericService!!.badgeNumber > 0 }?.genericService?.badgeNumber
                ?: 0,
            toolTag = ToolTag.GenericServiceTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.GenericServiceTask.name,
                    shiftId
                )
            } ?: run {
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.GenericServiceTask.name
                )
            }
        },
        SectionListItem(
            R.string.blood_exam_task,
            R.drawable.ic_blood_exam_task,
            viewModel.badges.firstOrNull { it.bloodExam != null && it.bloodExam!!.badgeNumber > 0 }?.bloodExam?.badgeNumber
                ?: 0,
            toolTag = ToolTag.BloodExamTask
        ) {
            viewModel.getSelectedShiftId()?.let { shiftId ->
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.BloodExamTask.name,
                    shiftId
                )
            } ?: run {
                navActions.navigateToTasksScreen(
                    Uri.encode(viewModel.patientCod),
                    ToolTag.BloodExamTask.name
                )
            }
        },
    )

    val sections = tools.filter { it.isActive }.sortedBy { it.priority }.mapNotNull { tool ->
        menuItems.firstOrNull { it.toolTag == tool.toolTag }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.downloadBadges()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = "") {
                onBack()
            }
        },
    ) { values ->
        if (viewModel.isLoading) {
            Column(
                modifier = Modifier
                    .padding(values)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    CircularProgressIndicator()
                }
            }

        } else {
            viewModel.caseDetails?.let { caseDetail ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(values)
                ) {
                    CaseInfoView(
                        imageRequestData = viewModel.requestImageRequestData,
                        patientCode = viewModel.patientCod ?: "",
                        caseDetail = caseDetail,
                        navActions = navActions,
                        clinicType = viewModel.clinicType,
                        hasOfflineData = viewModel.isDownloaded,
                        hasDataToSync = viewModel.isModified
                    )
                    Column(
                        modifier = Modifier.clickable {
                            viewModel.patientCod?.let {
                                navActions.navigateToAlertAndAllergies(
                                    Uri.encode(
                                        it
                                    )
                                )
                            }
                        }
                    ) {
                        HeaderIconText(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            iconId = R.drawable.ic_icon_alert,
                            textId = R.string.alert_allergies,
                            showArrow = true
                        )

                        AlertsChips(alerts = viewModel.alerts) {
                            viewModel.patientCod?.let {
                                navActions.navigateToAlertAndAllergies(
                                    Uri.encode(
                                        it
                                    )
                                )
                            }
                        }
                    }

                    HeaderIconText(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        iconId = R.drawable.ic_calendar_shift,
                        textId = R.string.calendar_shifts
                    )
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        val dateButtonValue: String =
                            if (DateUtils.isToday(viewModel.selectedDate)) stringResource(id = R.string.today) else DateFormat.format(
                                "dd.MM.yyyy",
                                Date(viewModel.selectedDate)
                            ).toString()
                        DropDownButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            value = dateButtonValue,
                            isEnabled = !viewModel.isLoading && viewModel.isOnline
                        ) {
                            showDatePicker = true
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        DropDownButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            value = viewModel.selectedShift?.name ?: stringResource(id = R.string.all),
                            isEnabled = !viewModel.isLoading && viewModel.isOnline
                        ) {
                            showShiftsPopup = true
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            content = {
                                items(sections) { sectionListItem ->
                                    SectionListItemView(item = sectionListItem)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            })
                    }
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = {
                                showDatePicker = false
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDatePicker = false
                                    viewModel.selectedDate = datePickerState.selectedDateMillis!!
                                    viewModel.downloadBadges()
                                }) {
                                    Text(text = "Conferma")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDatePicker = false
                                }) {
                                    Text(text = "Annulla")
                                }
                            }
                        ) {
                            DatePicker(
                                state = datePickerState
                            )
                        }
                    }
                    if (showShiftsPopup && (viewModel.shifts?.isNotEmpty() == true)) {
                        val popupItems = mutableListOf<ListPopupItem<OperatingShift>>(
                            ListPopupItem(
                                label = stringResource(id = R.string.all), item = null
                            )
                        )
                        popupItems.addAll(viewModel.shifts!!.map {
                            ListPopupItem(
                                label = it.name,
                                item = it
                            )
                        })
                        ListPopup(
                            title = stringResource(id = R.string.selectShift),
                            items = popupItems,
                            setShowDialog = {
                                showShiftsPopup = false
                            },
                            onItemSelected = {
                                showShiftsPopup = false
                                viewModel.modifiedShift = true
                                viewModel.selectedShift = it.item
                                viewModel.downloadBadges()
                            })
                    }
                    if (viewModel.errorMessage != null) {
                        ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                            viewModel.errorMessage = null
                        }, onRetry = {
                            viewModel.errorMessage = null
                            viewModel.downloadData()
                        })
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlertsChips(alerts: List<AlertItem>, onClick: () -> Unit) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(-10.dp, Alignment.Top),
        maxItemsInEachRow = 2
    ) {

        if (alerts.count() > 4) {
            alerts.take(3).forEach { alert ->
                AlertChip(alert = alert, maxLines = 1, fraction = 0.45f) {
                    onClick()
                }
            }
        } else {
            alerts.forEach { alert ->
                AlertChip(alert = alert, maxLines = 1, fraction = 0.45f) {
                    onClick()
                }
            }
        }
        if (alerts.count() > 4) {
            AssistChip(
                border = null,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,

                    ),
                label = {
                    Text(
                        text = "+${alerts.count() - 3}",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = {
                    onClick()
                }
            )
        }
    }
}

@Composable
private fun CaseInfoView(imageRequestData: ImageRequestData,patientCode: String,caseDetail: CaseDetail,navActions: NavigationActions,clinicType: ClinicType?, hasOfflineData: Boolean,hasDataToSync: Boolean){

    CompositionLocalProvider(
        LocalDensity provides Density(
            LocalDensity.current.density,
            1f // - we set here default font scale instead of system one
        )
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Column {
                PatientImage(
                    patientCode,
                    caseDetail.photo ?: "",
                    imageRequestData
                )
                Spacer(modifier = Modifier.height(8.dp))
                OfflineSyncImage(
                    hasOfflineData = hasOfflineData,
                    hasDataToSync = hasDataToSync
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .clickable {
                        navActions.navigateToPatientInfo(Uri.encode(patientCode))
                    }
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${caseDetail.surname} ${caseDetail.name}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${caseDetail.birthday} (${caseDetail.age})",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = caseDetail.gender.getIconId()),
                                contentDescription = ""
                            )
                        }
                        if (clinicType == ClinicType.CPA) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bed),
                                    contentDescription = ""
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = caseDetail.bed ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                text = caseDetail.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    DiaryButton() {
                        navActions.navigateToDiary(Uri.encode(patientCode))
                    }
                }
                if (clinicType == ClinicType.SPITEX) {
                    Text(
                        text = "${caseDetail.cap} ${caseDetail.locality}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
@Preview(
    fontScale = 2f
)
private fun PreviewCaseInfoView(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                CaseInfoView(
                    imageRequestData = ImageRequestData("",""),
                    patientCode = "",
                    caseDetail = CaseDetail(
                        address = "Via Calanchi 2 test indirizzo lunghissimo che on sc",
                        age = 97,
                        birthday = "28.12.1926",
                        cap = "6900",
                        contacts = CaseContacts("", listOf(),""),
                        externalMedics = listOf(),
                        gender = Gender("M", "Male",1),
                        id = 1,
                        internalMedics = listOf(),
                        locality = "Lugano da dsa da d ada dada d sa d ad dad dad",
                        name = "Elisa",
                        surname = "Santoro",
                        otherInfo = listOf(),
                        photo = null,
                        alerts = listOf(),
                        bed = "345"
                    ),
                    clinicType = ClinicType.CPA,
                    hasDataToSync = false,
                    hasOfflineData = false,
                    navActions = NavigationActions(NavController(LocalContext.current))
                )
            }
        }
    }
}

@Composable
private fun DiaryButton(onClick: () -> Unit){
    Button(
        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 20.dp),
        modifier = Modifier.height(32.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = md_theme_dark_secondaryContainer,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_diary),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(
                text = stringResource(id = R.string.diary),
                style = MaterialTheme.typography.labelLarge
            )
        }

    }
}
