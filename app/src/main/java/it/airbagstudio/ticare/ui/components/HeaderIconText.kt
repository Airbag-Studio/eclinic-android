package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun HeaderIconText(modifier: Modifier = Modifier,iconId: Int, textId: Int, showArrow: Boolean = false) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = stringResource(id = textId),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = textId),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline
        )
        if (showArrow) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
@Preview
private fun PreviewHeaderIconText(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it).padding(16.dp)) {
                HeaderIconText(
                    iconId = R.drawable.ic_field_calendar,
                    textId = R.string.calendar_shifts)
                HeaderIconText(
                    iconId = R.drawable.ic_icon_alert,
                    textId = R.string.alert_allergies)
            }
        }
    }
}

