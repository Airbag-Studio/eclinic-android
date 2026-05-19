package it.airbagstudio.ticare.pages.falls.list

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.DestinationsArgs
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.drugsAdministration.DrugAdministrationItemViewLoading
import it.airbagstudio.ticare.pages.wounds.list.WoundListItemView
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getCompleteName

@Composable
fun FallsListScreen(
    viewModel: FallsListViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val fallsList by viewModel.fallsList.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.reload()
            }
            else -> {}
        }
    }
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = viewModel.patient?.getCompleteName() ?: "") {
                onBack()
            }
        }
    ) { values ->

        Column(modifier = Modifier.padding(values)) {
            Text(
                text = viewModel.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${viewModel.date?.format("dd/MM/yyyy")} - ${viewModel.shiftName ?: stringResource(id = R.string.all)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            if (viewModel.isLoading) {
                repeat(8) {
                    DrugAdministrationItemViewLoading()
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 124.dp),
                    content = {
                        items(fallsList){
                            FallListItemView (item = it){woundId ->
                                /*
                                navigationActions.navigateToWoundDetails(

                                    Uri.encode(viewModel.patientCod),
                                    woundId,
                                    viewModel.genderId
                                )

                                 */
                            }
                            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                        }
                    })
            }
            if (viewModel.errorMessage != null){
                ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                    viewModel.errorMessage = null
                })
            }
        }

    }

}