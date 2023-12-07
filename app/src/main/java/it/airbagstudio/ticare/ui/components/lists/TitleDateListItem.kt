package it.airbagstudio.ticare.ui.components.lists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun TitleDateListItem(name:String, date:String,isEnabled:Boolean = true, onClick: () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .alpha(if (isEnabled) 1f else  0.5f)
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp)
    ) {
        Image(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = ""
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
        Spacer(modifier = Modifier.width(24.dp))
    }
}

@Composable
@Preview
private fun PreviewTitleDateListItem(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {
                TitleDateListItem(name = "Rimborso chilometrico \n dadadsa \n dsdasd", date = "20/08/2023") {

                }
                Divider()
                TitleDateListItem(name = "Rimborso chilometrico \n dadadsa \n dsdasd", date = "20/08/2023",isEnabled = false) {

                }
                Divider()
            }
        }

    }
}