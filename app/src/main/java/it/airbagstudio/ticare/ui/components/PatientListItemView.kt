package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.CaseInfo
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun PatientListItemView(patient: CaseInfo, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(vertical = 12.dp)
        ) {
            PatientImage(patient.photo)
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "${patient.birthday} (${patient.age})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${patient.surname} ${patient.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${patient.address}\n${patient.cap} ${patient.locality}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = patient.name
            )
        }
        Divider(modifier = Modifier.padding(start = 24.dp))
    }

}


@Composable
@Preview
fun PatientListItemViewLoading(){
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier

                    .width(56.dp)
                    .height(56.dp)
                    .background(shimmerBrush())
            ) {

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(shimmerBrush())
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth()
                        .background(shimmerBrush())
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(shimmerBrush())
                )
            }
        }
        Divider(modifier = Modifier.padding(start = 24.dp))
    }
}


@Composable
@Preview
private fun PreviewPatientListItemView() {
    AppTheme() {
        PatientListItemView(
            patient = CaseInfo(
                surname = "ABETE",
                name = "Maria",
                address = "Via la Montagna 16",
                cap = "6962",
                code = "23/2172",
                locality = "Viganello",
                birthday = "03.08.1936",
                age = 87,
                photo = null
                //photo = "https://www.tag24.it/wp-content/uploads/2023/04/WhatsApp-Image-2023-03-31-at-14.12.21-e1680618678712-800x560.jpeg"
            )
        ) {

        }
    }

}