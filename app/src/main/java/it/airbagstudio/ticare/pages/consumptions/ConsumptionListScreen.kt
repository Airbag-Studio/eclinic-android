package it.airbagstudio.ticare.pages.consumptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.Article
import ch.ticare.eclinic.library.entity.ClinicType
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.consumptions.create.ConsumptionCreateScreen
import it.airbagstudio.ticare.pages.consumptions.search.ConsumptionArticleSearch
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.components.lists.TitleDateListItem
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumptionListScreen(
    viewModel: ConsumptionListViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearchDialog by remember {
        mutableStateOf(false)
    }
    var showCreateDialog by remember {
        mutableStateOf(false)
    }
    var selectedArticle by remember {
        mutableStateOf<Article?>(null)
    }
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = stringResource(id = R.string.consumption_title)) {
                onBack()
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (uiState.clinicType == ClinicType.SPITEX) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.primary,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add, contentDescription = stringResource(
                                id = R.string.new_treatment
                            )
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(
                                id = R.string.new_consumption
                            )
                        )
                    },
                    onClick = {
                        showSearchDialog = true
                    })
            }
        }
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            if (uiState.isLoading) {
                repeat(8) {
                    PatientListItemViewLoading()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 124.dp),
                    content = {
                        items(uiState.consumptions.keys.toList()) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.titleMedium,
                                text = it
                            )
                            val items = uiState.consumptions.get(it)

                            items?.forEach { item ->
                                TitleDateListItem(
                                    name = item.item,
                                    date = item.date.toDate("dd.MM.yyyy")?.format("dd/MM/yyyy") ?: ""
                                ) {
                                    viewModel.selectedConsumption = item
                                    showCreateDialog = true
                                }
                                Divider(modifier = Modifier.padding(start = if (items.lastOrNull() == item) 0.dp else 16.dp))
                            }
                        }
                    })
            }
        }
    }
    if (showSearchDialog){
        ConsumptionArticleSearch(onDismissRequest = { article ->
            showSearchDialog = false
            selectedArticle = article
            if (article != null) {
                showCreateDialog = true
            }
        })
    }
    if (showCreateDialog){
        ConsumptionCreateScreen(
            article = selectedArticle,
            consumption = viewModel.selectedConsumption,
            onDismissRequest = { refreshList ->
                showCreateDialog = false
                selectedArticle = null
                viewModel.selectedConsumption = null
                if (refreshList) {
                    viewModel.downloadData()
                }
        })

    }
    if (uiState.errorMessage != null){
        ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
            viewModel.clearError()
        })
    }
}