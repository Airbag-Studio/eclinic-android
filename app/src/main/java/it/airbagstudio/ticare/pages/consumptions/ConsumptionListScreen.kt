package it.airbagstudio.ticare.pages.consumptions

import androidx.compose.foundation.Image
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
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.consumptions.create.ConsumptionCreateScreen
import it.airbagstudio.ticare.pages.consumptions.search.ConsumptionArticleSearch
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme

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
                            id = R.string.new_treatment
                        )
                    )
                },
                onClick = {
                    showSearchDialog = true
                })
        }
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 124.dp),
                content = {
                    items(uiState.consumptions){
                        ConsumptionListItem(name = "", date = "") {
                            viewModel.selectedConsumption = it
                        }
                    }
                })
        }
    }
    if (showSearchDialog){
        ConsumptionArticleSearch(onDismissRequest = {
            showSearchDialog = false
            selectedArticle = it
            showCreateDialog = true
        })
    }
    if (showCreateDialog){
        selectedArticle?.let {
            ConsumptionCreateScreen(it,onDismissRequest = { refreshList ->
                showCreateDialog = false
                selectedArticle = null
            })
        }
    }
}

@Composable
private fun ConsumptionListItem(name:String,date:String,onClick: () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Image(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = ""
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
        Spacer(modifier = Modifier.width(24.dp))
    }
}
@Composable
@Preview
private fun PreviewConsumptionListItem(){
    AppTheme {
        Column {
            ConsumptionListItem(name = "Rimborso chilometrico \n dadadsa \n dsdasd", date = "20/08/2023") {
                
            }
        }
    }
}