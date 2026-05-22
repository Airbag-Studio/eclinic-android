package it.airbagstudio.ticare.pages.falls.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.ClinicType
import ch.ticare.eclinic.library.entity.Fall
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.utils.FallField
import it.airbagstudio.ticare.utils.getLabelRes
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.PopupTextField
import it.airbagstudio.ticare.ui.components.SwitchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditFallDialogScreen(
    codCase: String,
    fall: Fall? = null,
    canWrite: Boolean,
    onDismissRequest: (Boolean) -> Unit,
    viewModel: CreateEditFallDialogScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.clearData()
        viewModel.codCase = codCase
        viewModel.downloadData(fall)
    }
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(false)
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.fall))
                    },
                    actions = {
                        IconButton(onClick = { onDismissRequest(false) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            }
        ) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(scrollState)
                    .imePadding()
                    .padding(16.dp)
            ) {
                // --- Data e ora ---
                CalendarTextField(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canWrite,
                    showTime = true,
                    date = uiState.dateTime,
                    label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                    onDateChanged = { viewModel.setDateTime(it) }
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- Persona che ha rilevato ---
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.detector,
                    onValueChange = { viewModel.setDetector(it) },
                    singleLine = true,
                    label = { Text(text = stringResource(id = R.string.fall_detector)) },
                    enabled = canWrite,
                )
                Spacer(modifier = Modifier.height(24.dp))
                // --- Luogo ---
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = FallField.Location.getLabelRes(uiState.clinicType)) + " *",
                    value = uiState.locationLabel ?: "",
                    items = viewModel.locations.value,
                    enabled = canWrite,
                ) { viewModel.setLocation(it.item) }
                Spacer(modifier = Modifier.height(24.dp))

                // --- Causa ---
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = FallField.Cause.getLabelRes(uiState.clinicType)) + " *",
                    value = uiState.causeLabel ?: "",
                    items = viewModel.causes.value,
                    enabled = canWrite,
                ) { viewModel.setCause(it.item) }
                Spacer(modifier = Modifier.height(24.dp))

                // --- Dettaglio Cause ---
                if(uiState.clinicType == ClinicType.CPA) {
                    PopupTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(id = FallField.CauseDetail.getLabelRes(uiState.clinicType)),
                        value = uiState.causeDetailLabel ?: "",
                        items = viewModel.causeDetails.value,
                        enabled = canWrite,
                    ) { viewModel.setCauseDetail(it.item) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // --- Illuminazione ---
                SwitchItem(
                    label = stringResource(id = R.string.fall_lighting) + " *",
                    value = uiState.lighting,
                    enabled = canWrite,
                    onChange = { viewModel.setLighting(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- Conseguenze ---
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = FallField.Consequences.getLabelRes(uiState.clinicType)) + " *",
                    value = uiState.consequencesLabel ?: "",
                    items = viewModel.consequences.value,
                    enabled = canWrite,
                ) { viewModel.setConsequences(it.item) }
                Spacer(modifier = Modifier.height(24.dp))

                // --- Dettaglio Conseguenze ---
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.consequenceDetails,
                    onValueChange = { viewModel.setConsequenceDetails(it) },
                    singleLine = true,
                    label = { Text(text = stringResource(id = FallField.ConsequencesDetails.getLabelRes(uiState.clinicType))) },
                    enabled = canWrite,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- Ricovero ---
                SwitchItem(
                    label = stringResource(id = R.string.fall_intervention) + " *",
                    value = uiState.intervention,
                    enabled = canWrite,
                    onChange = { viewModel.setIntervention(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                // --- Stato pre-caduta ---
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = FallField.PreStatus.getLabelRes(uiState.clinicType)) + " *",
                    value = uiState.preStatusLabel ?: "",
                    items = viewModel.preStatuses.value,
                    enabled = canWrite,
                ) { viewModel.setPreStatus(it.item) }
                Spacer(modifier = Modifier.height(8.dp))

                // --- Grado di Autonomia ---
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = FallField.AutonomyDegree.getLabelRes(uiState.clinicType)) + " *",
                    value = uiState.autonomyDegreeLabel ?: "",
                    items = viewModel.autonomyDegrees.value,
                    enabled = canWrite,
                ) { viewModel.setAutonomyDegree(it.item) }
                Spacer(modifier = Modifier.height(8.dp))

                // --- Deficit Visivo ---
                SwitchItem(
                    label = stringResource(id = R.string.fall_visual_issues) + " *",
                    value = uiState.visualIssues,
                    enabled = canWrite,
                    onChange = { viewModel.setVisualIssues(it) }
                )

                // --- Demenza/Disorientamento ---
                SwitchItem(
                    label = stringResource(id = R.string.fall_disorientation) + " *",
                    value = uiState.disorientation,
                    enabled = canWrite,
                    onChange = { viewModel.setDisorientation(it) }
                )

                // --- Contenzione ---
                SwitchItem(
                    label = stringResource(id = FallField.WithRestraint.getLabelRes(uiState.clinicType)) + " *",
                    value = uiState.withRestraint,
                    enabled = canWrite,
                    onChange = { viewModel.setWithRestraint(it) }
                )

                // --- Calzature antiscivolo ---
                SwitchItem(
                    label = stringResource(id = FallField.NonSlipShoes.getLabelRes(uiState.clinicType)) + " *",
                    value = uiState.nonSlipShoes,
                    enabled = canWrite,
                    onChange = { viewModel.setNonSlipShoes(it) }
                )

                // --- Con Testimoni ---
                SwitchItem(
                    label = stringResource(id = R.string.fall_with_witnesses) + " *",
                    value = uiState.withWitnesses,
                    enabled = canWrite,
                    onChange = { viewModel.setWithWitnesses(it) }
                )

                // --- Dettaglio testimoni (visibile solo se con testimoni) ---
                if (uiState.withWitnesses) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.witnesses,
                        onValueChange = { viewModel.setWitnesses(it) },
                        singleLine = true,
                        label = { Text(text = stringResource(id = FallField.FallWitnessesDetails.getLabelRes(uiState.clinicType))) },
                        enabled = canWrite,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // --- Notifiche ---
                SwitchItem(
                    label = stringResource(id = R.string.fall_medic_ack),
                    value = uiState.medicAck,
                    enabled = canWrite,
                    onChange = { viewModel.setMedicAck(it) }
                )
                SwitchItem(
                    label = stringResource(id = R.string.fall_family_ack),
                    value = uiState.familyAck,
                    enabled = canWrite,
                    onChange = { viewModel.setFamilyAck(it) }
                )
                SwitchItem(
                    label = stringResource(id = R.string.fall_ref_person_ack),
                    value = uiState.refPersonAck,
                    enabled = canWrite,
                    onChange = { viewModel.setRefPersonAck(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // --- Note ---
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    value = uiState.notes,
                    onValueChange = { viewModel.setNotes(it) },
                    label = { Text(text = stringResource(id = R.string.notes)) },
                    enabled = canWrite,
                )
                Spacer(modifier = Modifier.weight(1f))

                // --- Salva ---
                Button(
                    enabled = uiState.isValid && canWrite && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    onClick = { viewModel.saveFall() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.save)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.save))
                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (viewModel.errorMessage != null) {
            ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                viewModel.errorMessage = null
            })
        }
        if (uiState.isSuccess) {
            viewModel.clearData()
            onDismissRequest(true)
        }
    }
}
