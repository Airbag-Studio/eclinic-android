package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.CaseInfo
import ch.ticare.eclinic.library.entity.ClinicType
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientsList.PatientListUiState
import it.airbagstudio.ticare.ui.theme.AppTheme
import java.time.temporal.TemporalQueries.zone

@Composable
fun PatientListItemView(
    clinicType: ClinicType,
    patient: PatientListUiState.PatientUIState,
    requestImageRequestData: ImageRequestData,
    onClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(vertical = 12.dp)
        ) {
            Column() {
                PatientImage(patient.patientCode, patient.photo ?: "", requestImageRequestData)
                Spacer(modifier = Modifier.height(8.dp))
                OfflineSyncImage(
                    hasOfflineData = patient.hasDownloadedData,
                    hasDataToSync = patient.hasModifiedData
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Row {
                    Text(
                        text = patient.birthDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = patient.genderIconId),
                        contentDescription = ""
                    )
                }

                Text(
                    text = patient.completeName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(text = "# ${patient.patientCode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!patient.zoneName.isNullOrEmpty()) {
                        Row() {
                            Text(text = "Zona: ",
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Text(text = patient.zoneName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline)
                        }

                    }
                    if (!patient.microZoneName.isNullOrEmpty()) {
                        Row() {
                            Text(
                                text = " - ",
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Text(
                                text = patient.microZoneName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                if (clinicType == ClinicType.CPA) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bed),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = patient.bed ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight(700),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        /*
                        Text(
                            text = " - 2",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                         */
                    }
                } else {
                    Text(
                        text = patient.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = patient.patientCode
            )
        }
        HorizontalDivider(modifier = Modifier.padding(start = 24.dp))
    }

}


@Composable
@Preview
fun PatientListItemViewLoading() {
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
        HorizontalDivider(modifier = Modifier.padding(start = 24.dp))
    }
}
