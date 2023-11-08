package it.airbagstudio.ticare.pages.wounds.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

data class WoundListItem(
    val id: Int,
    val name:String,
    val date: String
)

@Composable
fun WoundListItemView(item: WoundListItem,onClick: (Int) -> Unit){
    Row(modifier = Modifier
        .clickable {
            onClick(item.id)
        }
        .padding(start = 16.dp, top = 12.dp, 24.dp, 12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
        
    }
}

@Composable
@Preview
private fun PreviewWoundListItemView(){
    AppTheme {
        Column(Modifier.background(Color.White)) {
            WoundListItemView(item = WoundListItem(0,"Lacerazione","23 agosto 2023")){}
        }
    }
}