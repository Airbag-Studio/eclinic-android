package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.md_theme_light_onSurfaceVariant
import it.airbagstudio.ticare.ui.theme.seed

@Composable
fun DropDownButton(modifier: Modifier = Modifier, value: String, onClick: () -> Unit){
    TextButton(
        shape = RoundedCornerShape(2),
        modifier = modifier.then(
            Modifier.border( width = 1.dp,
            color = seed,
            shape = RoundedCornerShape(4.dp)
            )),

        onClick = {
            onClick()
        }) {

        Text(
            modifier = Modifier.weight(1f),
            text = value,
            fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
            color = md_theme_light_onSurfaceVariant
        )
        Image(
            painter = painterResource(id = R.drawable.id_dropdown),
            contentDescription = value
        )

    }
}