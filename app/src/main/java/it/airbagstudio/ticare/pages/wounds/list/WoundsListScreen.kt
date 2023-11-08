package it.airbagstudio.ticare.pages.wounds.list

import android.net.Uri
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.drugsAdministration.DrugAdministrationItemViewLoading
import it.airbagstudio.ticare.pages.wounds.create.CreateWoundDialogScreen
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.getCompleteName

@Composable
fun WoundListScreen(
    viewModel: WoundListScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {
    var showCreateBottomSheet by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    showCreateBottomSheet = true
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = stringResource(
                            id = R.string.wounds
                        )
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = R.string.new_wounds
                        ),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        },
        topBar = {
            ToolbarWithBackAndSync(title = uiState.patientName ?: "") {
                onBack()
            }
        }

    ) { values ->

        Column(modifier = Modifier.padding(values)) {
            Text(
                text = stringResource(id = R.string.wounds),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${uiState.date} - ${uiState.shiftName ?: stringResource(id = R.string.all)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            if (uiState.isLoading) {
                repeat(8) {
                    DrugAdministrationItemViewLoading()
                    Divider(modifier = Modifier.padding(start = 16.dp))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 124.dp),
                    content = {
                    items(uiState.wounds){
                        WoundListItemView(item = it){woundId ->
                            navigationActions.navigateToWoundDetails(Uri.encode(viewModel.patientCod),woundId)
                        }
                        Divider(modifier = Modifier.padding(start = 16.dp))
                    }
                })
            }
            if (viewModel.errorMessage != null){
                ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                    viewModel.errorMessage = null
                })
            }
        }

        if (showCreateBottomSheet){
            CreateWoundDialogScreen(codCase = viewModel.patientCod,onDismissRequest = {success ->
                showCreateBottomSheet = false
                viewModel.downloadWounds()
            })
        }
    }
}