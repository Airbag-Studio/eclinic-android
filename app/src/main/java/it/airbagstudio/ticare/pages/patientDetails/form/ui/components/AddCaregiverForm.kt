package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.cbi.CbiFormUiState
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaregiverForm(
    uiState: CbiFormUiState.Editing, // Expecting Editing state for form fields
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onRelationshipChanged: (String) -> Unit,
    onContactChanged: (String) -> Unit
) {
    val relationshipOptions = listOf(
        stringResource(id = R.string.relationship_son_daughter),
        stringResource(id = R.string.relationship_father),
        stringResource(id = R.string.relationship_mother),
        stringResource(id = R.string.relationship_other)
    )
    var relationshipExpanded by remember { mutableStateOf(false) }

    Column {
        Text(
            stringResource(id = R.string.add_caregiver_form_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.newCaregiverFirstName,
            onValueChange = onFirstNameChanged,
            label = { Text(stringResource(id = R.string.label_first_name)) },
            isError = uiState.newCaregiverFirstNameError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        uiState.newCaregiverFirstNameError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.newCaregiverLastName,
            onValueChange = onLastNameChanged,
            label = { Text(stringResource(id = R.string.label_last_name)) },
            isError = uiState.newCaregiverLastNameError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        uiState.newCaregiverLastNameError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = relationshipExpanded,
            onExpandedChange = { relationshipExpanded = !relationshipExpanded }
        ) {
            OutlinedTextField(
                value = uiState.newCaregiverRelationship.ifEmpty { stringResource(id = R.string.select_relationship_hint) },
                onValueChange = {}, // Read-only, changed by dropdown selection
                readOnly = true,
                label = { Text(stringResource(id = R.string.label_relationship)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationshipExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                isError = uiState.newCaregiverRelationshipError != null
            )
            ExposedDropdownMenu(
                expanded = relationshipExpanded,
                onDismissRequest = { relationshipExpanded = false }
            ) {
                relationshipOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onRelationshipChanged(option)
                            relationshipExpanded = false
                        }
                    )
                }
            }
        }
        uiState.newCaregiverRelationshipError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.newCaregiverContact,
            onValueChange = onContactChanged,
            label = { Text(stringResource(id = R.string.label_contact_phone)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = uiState.newCaregiverContactError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        uiState.newCaregiverContactError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(id = R.string.button_cancel))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onSave) {
                Text(stringResource(id = R.string.button_save))
            }
        }
    }
}
