package it.airbagstudio.ticare.pages.patientDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.md_theme_dark_error
import it.airbagstudio.ticare.ui.theme.md_theme_dark_onError


data class SectionListItem(
    val nameId: Int,
    val iconId: Int,
    val badge: Int = 0,
    val onClick: () -> Unit
)

@Composable
fun SectionListItemView(item: SectionListItem) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(48.dp)
            .fillMaxWidth()
            .clip( RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                item.onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(32.dp),
            painter = painterResource(id = item.iconId),
            contentDescription = stringResource(id = item.nameId)
        )
        Text(
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            text = stringResource(id = item.nameId),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (item.badge > 0) {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    LocalDensity.current.density,
                    1f // - we set here default font scale instead of system one
                )
            ) {
                Text(
                    modifier = Modifier
                        .size(25.dp)
                        .background(
                            md_theme_dark_error,
                            RoundedCornerShape(15.dp)
                        ),
                    text = "${item.badge}",
                    lineHeight = 25.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = md_theme_dark_onError
                )
            }
        }
        Icon(
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 8.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = ""
        )
    }
}

@Composable
@Preview
private fun PreviewSectionListItemView() {
    AppTheme {
        Scaffold {
            Column(
                Modifier
                    .padding(it)
                    .background(MaterialTheme.colorScheme.secondaryContainer)) {
                SectionListItemView(
                    SectionListItem(
                        R.string.vital_parameters,
                        R.drawable.ic_vital_parameters,
                        1
                    ) {

                    })
            }
        }
    }
}