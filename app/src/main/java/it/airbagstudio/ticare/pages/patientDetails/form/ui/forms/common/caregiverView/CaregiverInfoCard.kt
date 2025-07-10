package it.airbagstudio.ticare.pages.patientDetails.form.ui.forms.common.caregiverView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.Contact
import it.airbagstudio.ticare.R

@Composable
fun CaregiverInfoCard(caregiver: Contact) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(id = R.string.caregiver_info_card_title),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("${stringResource(id = R.string.label_first_name)}: ${caregiver.fullname}")
            //Text("${stringResource(id = R.string.label_last_name)}: ${caregiver.lastName}")
            Text("${stringResource(id = R.string.label_relationship)}: ${caregiver.relationship}")
            Text("${stringResource(id = R.string.label_contact_phone)}: ${caregiver.phoneNumbers}")
        }
    }
}
