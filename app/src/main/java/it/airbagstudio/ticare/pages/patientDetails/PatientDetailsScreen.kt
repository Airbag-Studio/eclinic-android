package it.airbagstudio.ticare.pages.patientDetails

import android.net.Uri
import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key.Companion.U
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.data.AlertItem
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.DropDownButton
import it.airbagstudio.ticare.ui.components.PatientImage
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme
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


    var showDatePicker by remember {
        mutableStateOf(false)
    }
    val calendar = Calendar.getInstance()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    var selectedDate by remember {
        mutableLongStateOf(calendar.timeInMillis)
    }

    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = "") {
                onBack()
            }
        }
    ) { values ->
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
                PatientImage(imageUrl = null)
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
                        text = "Antonietti Raffaella",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "28.12.1926 (97)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Via Calanchi 2, 6900 Lugano",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AlertsChips(alerts = viewModel.alerts) {
                showBottomSheet = true
            }

            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
               val dateButtonValue : String = if (DateUtils.isToday(selectedDate)) stringResource(id = R.string.today) else DateFormat.format("dd.MM.yyyy",Date(selectedDate)).toString()
                DropDownButton(modifier = Modifier.weight(1f), value = dateButtonValue) {
                    showDatePicker = true
                }
                Spacer(modifier = Modifier.width(8.dp))
                DropDownButton(modifier = Modifier.weight(1f), value = "Colazione") {

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
                        modifier = Modifier.weight(1f)
                    ){
                        navActions.navigateToPatientInfo(Uri.encode("das/dad"))
                    }
                    VerticalDivider()
                    GridButton(
                        image = painterResource(id = R.drawable.ic_pills), label = stringResource(
                            id = R.string.drug_administration
                        ),
                        modifier = Modifier.weight(1f)
                    ){
                        navActions.navigateToDrugAdministration(Uri.encode("das/dad"))
                    }
                    VerticalDivider()
                    GridButton(
                        image = painterResource(id = R.drawable.ic_vital_parameters),
                        label = stringResource(
                            id = R.string.vital_parameters
                        ),
                        modifier = Modifier.weight(1f)
                    ){}
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
                        modifier = Modifier.weight(1f)
                    ){
                        navActions.navigateToAllergies(Uri.encode("das/dad"))
                    }
                    VerticalDivider()
                    GridButton(
                        image = painterResource(id = R.drawable.ic_diary), label = stringResource(
                            id = R.string.diary
                        ),
                        modifier = Modifier.weight(1f)
                    ){}
                    VerticalDivider()
                    GridButton(
                        image = painterResource(id = R.drawable.ic_care_planes),
                        label = stringResource(
                            id = R.string.care_planes
                        ),
                        modifier = Modifier.weight(1f)
                    ){}
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
                        modifier = Modifier.weight(1f),
                        badgeCount = 34
                    ){}
                    VerticalDivider()
                    GridButton(
                        image = painterResource(id = R.drawable.ic_wounds), label = stringResource(
                            id = R.string.wounds
                        ),
                        modifier = Modifier.weight(1f)
                    ){}
                    VerticalDivider()
                    GridButton(
                        image = painterResource(id = R.drawable.ic_other_prescriptions),
                        label = stringResource(
                            id = R.string.other_prescriptions
                        ),
                        modifier = Modifier.weight(1f)
                    ){}
                }
                Divider(color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.weight(1f))

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
                            selectedDate = datePickerState.selectedDateMillis!!
                        }) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                        }) {
                            Text(text = "Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState
                    )
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

@Composable
private fun GridButton(
    image: Painter,
    label: String,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Image(
                painter = image,
                contentDescription = label
            )
            if (badgeCount > 0) {
                Text(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .padding(horizontal = 5.dp),
                    text = "$badgeCount",
                    color = MaterialTheme.colorScheme.errorContainer,
                    textAlign = TextAlign.Center,
                    lineHeight = 25.sp
                )
            }
        }
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
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

        alerts.take(3).forEach { alert ->
            AlertChip(alert = alert) {
                onClick()
            }
        }
        if (alerts.count() > 3) {
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

@Composable
@Preview
private fun PreviewPatientDetailsScreen() {
    val vm = PatientDetailsScreenViewModel(savedStateHandle = SavedStateHandle())
    vm.alerts = listOf(
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri "
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Param"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parame"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
        AlertItem(
            colorBg = "#c40a13",
            colorFg = "#ffffff",
            label = "Parametri Vitali"
        ),
    )
    AppTheme {
        PatientDetailsScreen(viewModel = vm, navActions = NavigationActions(NavController(LocalContext.current))){}
    }
}