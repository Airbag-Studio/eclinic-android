package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun ActivityListItemView(title:String,onClick: () -> Unit){
    Column(Modifier.clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier.padding(16.dp,8.dp,24.dp,8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
        }
        Divider()
    }
}


@Composable
@Preview
private fun ItemPreview(){
    AppTheme {
        Column {
            ActivityListItemView(title = "Utilizzo MLL - Limitazioni meccaniche das ds d sa ds ad sa d") {

            }
            ActivityListItemView(title = "Titolo prestazione 2") {

            }
        }

    }
}