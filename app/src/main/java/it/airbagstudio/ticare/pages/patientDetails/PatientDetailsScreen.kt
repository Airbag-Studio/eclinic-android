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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleResumeEffect
import ch.ticare.eclinic.library.entity.OperatingShift
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.DropDownButton
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.HeaderIconText
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.components.PatientImage
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.md_theme_dark_secondaryContainer
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

    val sections = listOf(
        SectionListItem(
            R.string.vital_parameters,
            R.drawable.ic_vital_parameters,
            viewModel.badges.firstOrNull { it.vitalSign.badgeNumber > 0 }?.vitalSign?.badgeNumber
                ?: 0
        ) {
            navActions.navigateToVitalParameters(Uri.encode(viewModel.patientCod))
        },
        SectionListItem(
            R.string.drug_administration,
            R.drawable.ic_pills,
            viewModel.badges.firstOrNull { it.pharmacological.badgeNumber > 0 }?.pharmacological?.badgeNumber
                ?: 0
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
        ) {
            navActions.navigateToWounds(Uri.encode(viewModel.patientCod))
        },
        SectionListItem(
            R.string.care_planes,
            R.drawable.ic_care_planes,
            viewModel.badges.firstOrNull { it.carePlan.badgeNumber > 0 }?.carePlan?.badgeNumber
                ?: 0
        ) {
            navActions.navigateToCarePlans(Uri.encode(viewModel.patientCod))
        },
        SectionListItem(
            R.string.other_prescriptions,
            R.drawable.ic_other_prescriptions,
        ) {
            navActions.navigateToOtherServices(Uri.encode(viewModel.patientCod))
        }
    )


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
                    Column {
                        PatientImage(
                            viewModel.patientCod ?: "",
                            caseDetail?.photo ?: "",
                            viewModel.requestImageRequestData
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OfflineSyncImage(
                            hasOfflineData = viewModel.isDownloaded,
                            hasDataToSync = viewModel.isModified
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
                                viewModel.patientCod?.let {
                                    navActions.navigateToPatientInfo(Uri.encode(it))
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Row {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "${caseDetail?.surname} ${caseDetail?.name}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${caseDetail?.birthday} (${caseDetail?.age})",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_agender),
                                        contentDescription = ""
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_bed),
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = md_theme_dark_secondaryContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                onClick = {
                                    viewModel.patientCod?.let {
                                        navActions.navigateToDiary(Uri.encode(it))
                                    }
                                }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bed),
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
                }
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
            .padding(horizontal = 16.dp),
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


