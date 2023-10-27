package it.airbagstudio.ticare.pages.carePlans.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun CarePlanDetailsScreen(
    onBack: () -> Unit
){
    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = stringResource(id = R.string.care_planes)) {
                onBack()
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                expanded = true,
                text = { Text(
                    text = stringResource(id = R.string.new_care),
                    style = MaterialTheme.typography.labelLarge
                ) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "") },
                contentColor = MaterialTheme.colorScheme.primary,
                onClick = {  })
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            Divider()
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rinforzo Muscolare",//TODO modificare
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Divider()
            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.opening),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "10/06/2022",//TODO modificare
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }
            Divider()
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.cares),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
@Preview
private fun PreviewCarePlanDetailsScreen(){
    AppTheme {
        CarePlanDetailsScreen(){}
    }
}