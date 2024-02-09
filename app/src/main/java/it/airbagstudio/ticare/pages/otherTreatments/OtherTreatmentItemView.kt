package it.airbagstudio.ticare.pages.otherTreatments

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.theme.AppTheme

data class OtherTreatmentItem(val name:String,val description:String,val number:String,val id:Int = 0,val hasDataToUpload: Boolean)

@Composable
fun OtherTreatmentItemView(item:OtherTreatmentItem,isSearch: Boolean = false,onClick:()-> Unit){
    Column(modifier = Modifier
        .padding(start = 16.dp)
        .clickable {
            onClick()
        }) {
        Row(
            modifier = Modifier.padding(end = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isSearch) {
                Column {
                    Image(painter = painterResource(id = R.drawable.ic_check), contentDescription = "")
                    if (item.hasDataToUpload){
                        Spacer(modifier = Modifier.height(2.dp))
                        OfflineSyncImage(hasOfflineData = true, hasDataToSync = true)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.number,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
        HorizontalDivider()
    }

}

@Composable
@Preview
private fun PreviewOtherTreatmentItemView(){
    AppTheme() {
        Surface() {
            OtherTreatmentItemView(item = OtherTreatmentItem("Medicamento Forfait", description = "Forfait per prestazioni terapeutiche Grado 02", number = "FPT01", hasDataToUpload = true)){

            }
        }

    }
}
@Composable
@Preview
private fun PreviewOtherTreatmentItemViewSearch(){
    AppTheme() {
        Surface() {
            OtherTreatmentItemView(item = OtherTreatmentItem("Medicamento Forfait", description = "Forfait per prestazioni terapeutiche Grado 02", number = "FPT01", hasDataToUpload = true), isSearch = true){

            }
        }

    }
}