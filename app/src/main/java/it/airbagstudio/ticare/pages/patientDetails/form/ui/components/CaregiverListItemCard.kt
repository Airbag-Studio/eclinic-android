package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.Caregiver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverListItemCard(caregiver: Caregiver, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "${caregiver.firstName} ${caregiver.lastName}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "${stringResource(id = R.string.label_relationship)}: ${caregiver.relationship}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "${stringResource(id = R.string.label_contact_phone)}: ${caregiver.contact}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
