package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.Caregiver
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi.CbiFormUiState
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverSelectionModalSheet(
    uiState: CbiFormUiState.Editing,
    onDismiss: () -> Unit,
    onCaregiverSelected: (Caregiver) -> Unit,
    onAddNewCaregiverClick: () -> Unit,
    onSaveNewCaregiver: () -> Unit,
    onCancelAddCaregiver: () -> Unit,
    onNewCaregiverFirstNameChanged: (String) -> Unit,
    onNewCaregiverLastNameChanged: (String) -> Unit,
    onNewCaregiverRelationshipChanged: (String) -> Unit,
    onNewCaregiverContactChanged: (String) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding() // Ensures content is above navigation bars
                .imePadding() // Adjusts padding when keyboard is shown
        ) {
            if (uiState.isAddingCaregiver) {
                AddCaregiverForm(
                    uiState = uiState,
                    onSave = onSaveNewCaregiver,
                    onCancel = onCancelAddCaregiver,
                    onFirstNameChanged = onNewCaregiverFirstNameChanged,
                    onLastNameChanged = onNewCaregiverLastNameChanged,
                    onRelationshipChanged = onNewCaregiverRelationshipChanged,
                    onContactChanged = onNewCaregiverContactChanged
                )
            } else {
                // List View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(id = R.string.modal_title_select_caregiver),
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = onAddNewCaregiverClick) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.modal_add_caregiver_icon_description)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(id = R.string.modal_button_add_caregiver))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.availableCaregivers.isEmpty()) {
                    Text(stringResource(id = R.string.modal_empty_caregiver_list))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false) // Allows column to scroll within modal
                    ) {
                        items(uiState.availableCaregivers, key = { it.id }) { caregiver ->
                            CaregiverListItemCard(
                                caregiver = caregiver,
                                onClick = { onCaregiverSelected(caregiver) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Ensure some padding at the bottom
        }
    }
}
