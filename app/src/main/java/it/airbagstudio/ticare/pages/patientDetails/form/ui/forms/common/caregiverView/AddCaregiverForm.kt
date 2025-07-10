package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.common.caregiverView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaregiverForm(
    viewModel: CaregiverFromViewModel,
    onCancel: () -> Unit
) {
    val uiState by viewModel.contactCreateUiState.collectAsState()

    val relationship = viewModel.relationships.collectAsState()
    var relationshipExpanded by remember { mutableStateOf(false) }

    Column {
        Text(
            stringResource(id = R.string.add_caregiver_form_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.name,
            onValueChange = {
                viewModel.setContactName(it)
            },
            label = { Text(stringResource(id = R.string.label_first_name)) },
            isError = uiState.name.isEmpty(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        /*
        if(uiState.name.isEmpty()) {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

         */
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.surname,
            onValueChange = {
                viewModel.setContactSurname(it)
            },
            label = { Text(stringResource(id = R.string.label_last_name)) },
            isError = uiState.surname.isEmpty(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        /*
        uiState.newCaregiverLastNameError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

         */
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = relationshipExpanded,
            onExpandedChange = { relationshipExpanded = !relationshipExpanded }
        ) {
            OutlinedTextField(
                value = relationship.value.find { it.iD == uiState.idRelationship }?.name ?: stringResource(id = R.string.select_relationship_hint),
                onValueChange = {}, // Read-only, changed by dropdown selection
                readOnly = true,
                label = { Text(stringResource(id = R.string.label_relationship)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationshipExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                isError = uiState.idRelationship < 0
            )
            ExposedDropdownMenu(
                expanded = relationshipExpanded,
                onDismissRequest = { relationshipExpanded = false }
            ) {
                relationship.value.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name) },
                        onClick = {
                            viewModel.setContactRelationship(option.iD)
                            relationshipExpanded = false
                        }
                    )
                }
            }
        }
        /*
        uiState.newCaregiverRelationshipError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

         */
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.telephone,
            onValueChange = {
                viewModel.setContactTelephone(it)
            },
            label = { Text(stringResource(id = R.string.label_contact_phone)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = uiState.telephone.isEmpty(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        /*
        uiState.newCaregiverContactError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

         */
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(id = R.string.button_cancel))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                enabled = uiState.isValid,
                onClick = {
                viewModel.saveNewCaregiver()
                onCancel()
            }) {
                Text(stringResource(id = R.string.button_save))
            }
        }
    }
}
