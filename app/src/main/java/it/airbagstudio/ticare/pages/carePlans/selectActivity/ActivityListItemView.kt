package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.shimmerBrush
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun ActivityListItemView(isSelecting: Boolean, isSelected: Boolean, title:String, isTransferRow: Boolean = false, onClick: () -> Unit,onSelectedChange: (Boolean) -> Unit){
    val modifier = if (isTransferRow){
        Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
    }else{
        Modifier
    }

    // La riga di trasferta è ingrandita per essere imputabile facilmente da smartphone (TS1-3)
    val rowModifier = if (isTransferRow){
        Modifier
            .heightIn(min = 80.dp)
            .padding(16.dp,16.dp,24.dp,16.dp)
    }else{
        Modifier.padding(16.dp,8.dp,24.dp,8.dp)
    }

    val titleStyle = if (isTransferRow){
        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
    }else{
        MaterialTheme.typography.bodyLarge
    }


    Column(modifier.clickable {
        onClick()
    }) {
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // La trasferta non ha checkbox nemmeno in selezione: va registrata toccando la
            // riga, che apre il calcolo del tempo di trasferimento. Selezionarla insieme alle
            // altre prestazioni le farebbe imputare un minutaggio sbagliato.
            if (isSelecting && !isTransferRow){
                Checkbox(isSelected, onCheckedChange = {
                    onSelectedChange(it)
                })
            }
            if (isTransferRow){
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_running),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            }
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = titleStyle
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
        }
        HorizontalDivider()
    }
}


@Composable
fun ActivityListItemViewLoading(){
    Column {
        Row(
            modifier = Modifier.padding(16.dp,8.dp,24.dp,8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .background(shimmerBrush()),
                text = "",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        HorizontalDivider()
    }
}


@Composable
@Preview
private fun ItemPreview(){
    AppTheme {
        Column {
            ActivityListItemView(isSelecting = true, title = "Utilizzo MLL - Limitazioni meccaniche das ds d sa ds ad sa d", isSelected = true, onClick = {

            }) {

            }
            ActivityListItemView(isSelecting = true,title = "Titolo prestazione 2", isSelected = false, onClick = {

            }) {

            }
            ActivityListItemView(isSelecting = true,title = "Tempo trasferta", isTransferRow = true, isSelected = false, onClick = {

            }) {

            }
        }

    }
}