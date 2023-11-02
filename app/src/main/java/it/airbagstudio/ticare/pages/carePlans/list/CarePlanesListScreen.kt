package it.airbagstudio.ticare.pages.carePlans.list

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.carePlans.details.CarePlanCoursesListItemView
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync

@Composable
fun CarePlanesListScreen(

    viewModel: CarePlanesListScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
            onBack: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = uiState.patientName) {
                onBack()
            }
        }
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.care_planes),
                style = MaterialTheme.typography.headlineSmall

            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            if (uiState.isLoading) {
                repeat(8) {
                    PatientListItemViewLoading()
                }
            } else {
                LazyColumn(content = {
                    items(uiState.items){
                        CarePlanesListItemView(item = it, onClick = { id ->
                            navigationActions.navigateToCarePlanDetailsScreen(Uri.encode(viewModel.patientCod),id.toString())
                        })
                        Divider()
                    }
                })
            }
        }
        if (uiState.errorMessage != null){
            ErrorAlert(message = uiState.errorMessage ?: "", onDismissRequest = {
                viewModel.clearError()
            }, onRetry = {
                viewModel.downloadData()
            })
        }
    }
}