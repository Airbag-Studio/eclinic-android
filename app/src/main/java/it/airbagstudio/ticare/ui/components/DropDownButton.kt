package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun DropDownButton(modifier: Modifier = Modifier, value: String,isEnabled: Boolean, onClick: () -> Unit) {
    TextButton(
        enabled = isEnabled,
        shape = RoundedCornerShape(2),
        modifier = modifier
            .then(
                Modifier
                    .border(
                        width = 1.dp,
                        color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(vertical = 4.dp)
            ),
        onClick = {
            onClick()
        }) {
        Text(
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Image(
            painter = painterResource(id = R.drawable.id_dropdown),
            contentDescription = value
        )
    }
}

@Preview
@Composable
private fun DropDownButtonPreview(){
    AppTheme() {
        Column(Modifier.width(150.dp)) {
            DropDownButton(value = "Sera dopo cena", isEnabled = false) {

            }
        }

    }

}