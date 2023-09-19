package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.md_theme_light_inverseOnSurface
import it.airbagstudio.ticare.ui.theme.md_theme_light_onSurface
import it.airbagstudio.ticare.ui.theme.md_theme_light_onSurfaceVariant
import it.airbagstudio.ticare.ui.theme.md_theme_light_primary
import it.airbagstudio.ticare.ui.theme.md_theme_light_primaryContainer

@Composable
fun PatientListItemView(patient: PatientListItem, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(vertical = 12.dp)
        ) {
            PatientImage(imageUrl = patient.photo)
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "${patient.birthday} (${patient.age})",
                    style = MaterialTheme.typography.labelMedium,
                    color = md_theme_light_onSurfaceVariant
                )
                Text(
                    text = "${patient.surname} ${patient.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = md_theme_light_onSurface
                )
                Text(
                    text = "${patient.address}\n${patient.cAP} ${patient.locality}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = md_theme_light_onSurfaceVariant
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
private fun PreviewPatientListItemView() {
    AppTheme() {
        PatientListItemView(
            patient = PatientListItem(
                surname = "ABETE",
                name = "Maria",
                address = "Via la Montagna 16",
                cAP = "6962",
                cOD = "23/2172",
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