package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BuildPageHeader(title:String,date: String, shiftName: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = title,
        style = MaterialTheme.typography.headlineSmall

    )
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "$date - $shiftName",
        style = MaterialTheme.typography.labelMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    Divider()
}