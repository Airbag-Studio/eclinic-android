package it.airbagstudio.ticare.pages.wounds.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R

@Composable
fun TitleValueView(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    singleLine: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val clicable = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }
    Row(
        clicable.padding(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onClick != null) {
            Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
        }
    }

}