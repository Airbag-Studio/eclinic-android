package it.airbagstudio.ticare.pages.patientDetails

import android.net.Uri
import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ch.ticare.eclinic.library.entity.OperatingShift
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.DropDownButton
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.components.PatientImage
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.copy
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    viewModel: PatientDetailsScreenViewModel = hiltViewModel(),
    navActions: NavigationActions,
    onBack: () -> Unit
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showShiftsPopup by remember { mutableStateOf(false) }


    var showDatePicker by remember {
        mutableStateOf(false)
    }
    val calendar = Calendar.getInstance()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current



    DisposableEffect(lifecycleOwner){
        val observer = LifecycleEventObserver{ source, event ->
            if (event == Lifecycle.Event.ON_RESUME){
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
            val caseDetail = viewModel.caseDetails
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(values)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    PatientImage(
                        viewModel.patientCod ?: "",
                        caseDetail?.photo ?: "",
                        viewModel.requestImageRequestData
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${caseDetail?.surname} ${caseDetail?.name}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${caseDetail?.birthday} (${caseDetail?.age})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${caseDetail?.address}, ${caseDetail?.cap} ${caseDetail?.locality}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AlertsChips(alerts = viewModel.alerts) {
                    showBottomSheet = true
                }

                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val dateButtonValue: String =
                        if (DateUtils.isToday(viewModel.selectedDate)) stringResource(id = R.string.today) else DateFormat.format(
                            "dd.MM.yyyy",
                            Date(viewModel.selectedDate)
                        ).toString()
                    DropDownButton(
                        modifier = Modifier.weight(1f),
                        value = dateButtonValue,
                        isEnabled = !viewModel.isLoading
                    ) {
                        showDatePicker = true
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    DropDownButton(
                        modifier = Modifier.weight(1f),
                        value = viewModel.selectedShift?.name ?: stringResource(id = R.string.all),
                        isEnabled = !viewModel.isLoading
                    ) {
                        showShiftsPopup = true
                    }
                }

                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.primary)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        GridButton(
                            image = painterResource(id = R.drawable.ic_patient_info),
                            label = stringResource(
                                id = R.string.patient_info
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f)
                        ) {
                            navActions.navigateToPatientInfo(Uri.encode(viewModel.patientCod))
                        }
                        VerticalDivider()
                        GridButton(
                            image = painterResource(id = R.drawable.ic_pills),
                            label = stringResource(
                                id = R.string.drug_administration
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f),
                            badgeCount = viewModel.badges.firstOrNull { it.pharmacological.badgeNumber > 0 }?.pharmacological?.badgeNumber ?: 0
                        ) {

                            viewModel.selectedShift?.let {shift ->
                                navActions.navigateToDrugAdministration(Uri.encode(viewModel.patientCod),viewModel.selectedDate,shift.startTime,shift.stopTime, shiftName = shift.name)
                            } ?: run {
                                navActions.navigateToDrugAdministration(Uri.encode(viewModel.patientCod),viewModel.selectedDate,shiftName = context.getString(R.string.all))
                            }

                        }
                        VerticalDivider()
                        GridButton(
                            image = painterResource(id = R.drawable.ic_vital_parameters),
                            label = stringResource(
                                id = R.string.vital_parameters
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f),
                            badgeCount = viewModel.badges.firstOrNull { it.vitalSign.badgeNumber > 0 }?.vitalSign?.badgeNumber ?: 0
                        ) {
                            navActions.navigateToVitalParameters(Uri.encode(viewModel.patientCod))
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.primary)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        GridButton(
                            image = painterResource(id = R.drawable.ic_allergies),
                            label = stringResource(
                                id = R.string.allergies
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f)
                        ) {
                            navActions.navigateToAllergies(Uri.encode(viewModel.patientCod))
                        }
                        VerticalDivider()
                        GridButton(
                            image = painterResource(id = R.drawable.ic_diary),
                            label = stringResource(
                                id = R.string.diary
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f),
                        ) {
                            navActions.navigateToDiary(Uri.encode(viewModel.patientCod))
                        }
                        VerticalDivider()
                        GridButton(
                            image = painterResource(id = R.drawable.ic_care_planes),
                            label = stringResource(
                                id = R.string.care_planes
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f),
                            badgeCount = viewModel.badges.firstOrNull { it.carePlan.badgeNumber > 0 }?.carePlan?.badgeNumber ?: 0
                        ) {
                            navActions.navigateToCarePlans(Uri.encode(viewModel.patientCod))
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.primary)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        GridButton(
                            image = painterResource(id = R.drawable.ic_nursing_courses),
                            label = stringResource(
                                id = R.string.nursing_courses
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.selectedShift?.let {shift ->
                                navActions.navigateToNursingCourses(Uri.encode(viewModel.patientCod),viewModel.selectedDate,shift.startTime,shift.stopTime, shiftName = shift.name)
                            } ?: run {
                                navActions.navigateToNursingCourses(Uri.encode(viewModel.patientCod),viewModel.selectedDate, shiftName = context.getString(R.string.all))
                            }
                        }
                        VerticalDivider()
                        GridButton(
                            image = painterResource(id = R.drawable.ic_wounds),
                            label = stringResource(
                                id = R.string.wounds
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f)
                        ) {
                            navActions.navigateToWounds(Uri.encode(viewModel.patientCod))
                        }
                        VerticalDivider()
                        GridButton(
                            image = painterResource(id = R.drawable.ic_other_prescriptions),
                            label = stringResource(
                                id = R.string.other_prescriptions
                            ),
                            isLoading = viewModel.isLoadingActivities,
                            modifier = Modifier.weight(1f),
                        ) {
                            navActions.navigateToOtherServices(Uri.encode(viewModel.patientCod))
                        }
                    }
                }

                if (showBottomSheet) {
                    AlertsBottomSheet(state = sheetState, alerts = viewModel.alerts) {
                        showBottomSheet = false
                    }
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
                    val popupItems = mutableListOf<ListPopupItem<OperatingShift>>(ListPopupItem(label = stringResource(id = R.string.all), item = null))
                    popupItems.addAll(viewModel.shifts!!.map { ListPopupItem(label = it.name, item = it) })
                    ListPopup(
                        title = stringResource(id = R.string.selectShift),
                        items = popupItems,
                        setShowDialog = {
                            showShiftsPopup = false
                        },
                        onItemSelected = {
                            showShiftsPopup = false
                            viewModel.selectedShift = it.item
                            viewModel.downloadBadges()
                        })
                }
                if (viewModel.errorMessage != null){
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

@Composable
fun VerticalDivider() {
    Divider(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxHeight()  //fill the max height
            .width(1.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlertsChips(alerts: List<AlertItem>, onClick: () -> Unit) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 16.dp)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        maxItemsInEachRow = 2
    ) {

        if (alerts.count() > 4) {
            alerts.take(3).forEach { alert ->
                AlertChip(alert = alert) {
                    onClick()
                }
            }
        } else {
            alerts.forEach { alert ->
                AlertChip(alert = alert) {
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
                    Text(text = "+${alerts.count() - 3}")
                },
                onClick = { onClick() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AlertsBottomSheet(
    state: SheetState,
    alerts: List<AlertItem>,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state
    ) {
        // Sheet content
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.alerts),
            style = MaterialTheme.typography.titleLarge
        )
        FlowRow(
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            alerts.forEach { alert ->
                AlertChip(alert) {

                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AlertChip(alert: AlertItem, onClick: () -> Unit) {
    AssistChip(
        border = null,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(alert.colorBg.toColorInt()),
            labelColor = Color(alert.colorFg.toColorInt()),

            ),
        label = {
            Text(text = alert.label)
        },
        onClick = onClick
    )
}

