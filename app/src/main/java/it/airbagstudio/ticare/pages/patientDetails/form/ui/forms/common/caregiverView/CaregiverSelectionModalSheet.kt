package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.common.caregiverView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.Contact
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverSelectionModalSheet(
    viewModel: CaregiverFromViewModel,
    onDismiss: () -> Unit,
    onCaregiverSelected: (Contact) -> Unit,
    onCancelAddCaregiver: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showAddCaregiverForm by remember { mutableStateOf(false) }

    var availableCaregivers = viewModel.caregivers.collectAsState()

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
            if (showAddCaregiverForm) {
                AddCaregiverForm(
                    viewModel = viewModel,
                    onCancel = {
                        showAddCaregiverForm = false
                    },
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
                    TextButton(onClick = {
                        showAddCaregiverForm = true
                    }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.modal_add_caregiver_icon_description)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(id = R.string.modal_button_add_caregiver))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (availableCaregivers.value.isEmpty()) {
                    Text(stringResource(id = R.string.modal_empty_caregiver_list))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false) // Allows column to scroll within modal
                    ) {
                        items(availableCaregivers.value, key = { it.id }) { caregiver ->
                            CaregiverListItemCard(
                                caregiver = caregiver,
                                onClick = {
                                    onCaregiverSelected(caregiver)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Ensure some padding at the bottom
        }
    }
}
