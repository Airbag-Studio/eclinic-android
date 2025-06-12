package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.common.caregiverView

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
import ch.ticare.eclinic.library.entity.Contact
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverListItemCard(caregiver: Contact, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                caregiver.fullname,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "${stringResource(id = R.string.label_relationship)}: ${caregiver.relationship}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "${stringResource(id = R.string.label_contact_phone)}: ${caregiver.phoneNumbers}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
