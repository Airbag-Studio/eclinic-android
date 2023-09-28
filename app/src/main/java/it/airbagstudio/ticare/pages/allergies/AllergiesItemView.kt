package it.airbagstudio.ticare.pages.allergies

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.shimmerBrush
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun AllergiesItemView(allergiesItem: AllergiesItem) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .padding(start = 16.dp, end = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            alpha = if (allergiesItem.isDrug) 1f else 0f,
            painter = painterResource(id = R.drawable.ic_pill),
            contentDescription = allergiesItem.name
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = allergiesItem.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
@Preview
fun AllergiesItemViewLoading(){
    Row(
        modifier = Modifier
            .height(50.dp)
            .padding(start = 16.dp, end = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .background(shimmerBrush())
        ) {

        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .background(shimmerBrush())
        )
    }
}

@Composable
@Preview
private fun AllergiesItemViewPreview() {
    AppTheme {
        AllergiesItemView(allergiesItem = AllergiesItem("Penicillina", isDrug = true))
    }
}