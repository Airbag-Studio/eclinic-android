package it.airbagstudio.ticare.pages.patientInfo

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme


@Composable
fun PatientInfoCard(
    tile: String,
    text: String? = null,
    address: String? = null,
    phones: List<String> = listOf()
) {
    val ctx = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable {
                if(!address.isNullOrEmpty()){
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=$address")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    ctx.startActivity(mapIntent)
                }else if(phones.count() == 1){
                    startCall(phones.first(),ctx)
                }
            }
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(vertical = 8.dp)
    ) {
        Column(

            modifier = Modifier.weight(1f)
        ) {

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = tile,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            text?.let { content ->
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            address?.let { patientAddress ->
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = patientAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (phones.count() == 1) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = phones.first(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                phones.forEach { phone ->
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = Modifier
                            .clickable {
                                startCall(phone, ctx)
                            }
                            .padding(vertical = 8.dp, horizontal = 16.dp)

                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = phone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_call),
                            contentDescription = ""
                        )
                    }
                    if (phones.last() != phone) {
                        Divider()
                    }
                }
            }

        }
        if (address != null) {
            Image(
                modifier = Modifier.padding(end = 16.dp),
                painter = painterResource(id = R.drawable.ic_directions),
                contentDescription = ""
            )
        } else if (phones.count() == 1) {
            Image(
                modifier = Modifier.padding(end = 16.dp),
                painter = painterResource(id = R.drawable.ic_call),
                contentDescription = ""
            )
        }
    }


}

private fun startCall(number: String, context: Context) {
    val dialIntent = Intent(Intent.ACTION_DIAL)
    dialIntent.data = Uri.parse("tel:$number")
    context.startActivity(dialIntent)
}

@Composable
@Preview
private fun PreviewPatientInfoCardAddress() {
    AppTheme {
        PatientInfoCard(tile = "Indirizzo", address = "Via Clanchi 2, 6900 Lugano")
    }
}

@Composable
@Preview
private fun PreviewPatientInfoCardPhone() {
    AppTheme {
        PatientInfoCard(tile = "Figlio Matteo", phones = listOf("079/2149547"))
    }
}

@Composable
@Preview
private fun PreviewPatientInfoCardMultiplePhone() {
    AppTheme {
        PatientInfoCard(
            tile = "Dott.ssa Lina Sastri",
            phones = listOf("079/2149547", "091 923 75 61")
        )
    }
}