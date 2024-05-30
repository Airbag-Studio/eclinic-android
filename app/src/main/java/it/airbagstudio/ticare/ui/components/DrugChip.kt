package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DrugChip(label: String, textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant, includePadding: Boolean = true) {
    Text(
        modifier = Modifier
            .padding(
                horizontal = if (includePadding) 4.dp else 0.dp,
                vertical = if (includePadding) 2.dp else 0.dp
            )
            .border(
                width = 0.5.dp,
                color = Color(0xFF41484D),
                shape = RoundedCornerShape(size = 99.dp)
            )
            .padding(vertical = 2.dp, horizontal = 8.dp),
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}