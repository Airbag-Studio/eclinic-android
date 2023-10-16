package it.airbagstudio.ticare.pages.allergies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync

@Composable
fun AllergiesScreen(
    viewModel: AllergiesScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = "${viewModel.caseInfo?.name ?: ""} ${viewModel.caseInfo?.surname ?: ""}") {
                onBack()
            }
        }
    ) { values ->
        Column(modifier = Modifier.padding(values)) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                text = stringResource(id = R.string.allergies),
                style = MaterialTheme.typography.headlineSmall
            )
            if (viewModel.isLoading) {
                repeat(8) {
                    AllergiesItemViewLoading()
                }
            } else if (viewModel.allergies != null) {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(viewModel.allergies!!) {
                        AllergiesItemView(allergiesItem = it)
                        if (viewModel.allergies!!.last() != it) {
                            Divider()
                        }
                    }
                }
            }
        }
        if (viewModel.errorMessage != null) {
            ErrorAlert(
                message = viewModel.errorMessage!!,
                onDismissRequest = { viewModel.errorMessage = null },
                onRetry = {
                    viewModel.downloadAllergies()
                })
        }
    }
}

/*@Composable
@Preview
private fun AllergiesScreenPreview() {
    val viewModel = AllergiesScreenViewModel(SavedStateHandle())
    viewModel.allergies = listOf(
        AllergiesItem(name = "Pennicilina", isDrug = true),
        AllergiesItem(name = "Acido acetilsalicilico ed altri FANS", isDrug = true),
        AllergiesItem(name = "Arachidi", isDrug = false),
    )
    AppTheme {
        AllergiesScreen(
            navigationActions = NavigationActions(
                navController = NavController(
                    LocalContext.current
                )
            ),
            viewModel = viewModel
        ) {

        }
    }
}*/