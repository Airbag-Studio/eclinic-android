package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun DropDownButton(modifier: Modifier = Modifier, value: String, onClick: () -> Unit) {
    TextButton(
        shape = RoundedCornerShape(2),
        modifier = modifier

            .then(
                Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(vertical = 4.dp)
            ),

        onClick = {
            onClick()
        }) {

        Text(
            modifier = Modifier.weight(1f),
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
        DropDownButton(value = "Micro zone") {

        }
    }

}