package it.airbagstudio.ticare.pages.carePlans.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.HomeCareActivity
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.carePlans.create.CreateEditCareScreen
import it.airbagstudio.ticare.pages.carePlans.selectActivity.SelectCareActivityPopupScreen
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.components.timeTracker.StartTrackerDialog
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel
import it.airbagstudio.ticare.ui.theme.AppTheme
import kotlinx.serialization.json.JsonNull.content

@Composable
fun CarePlanDetailsScreen(
    viewModel : CarePlanDetailsScreenViewModel = hiltViewModel(),

    trackerViewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val trackerUiState by trackerViewModel.uiState.collectAsStateWithLifecycle()
    var showSelectNewActivityPopup by remember {
        mutableStateOf(false)
    }
    var showCreateCarePopup by remember {
        mutableStateOf(false)
    }
    var showStartTrackingPopup by remember {
        mutableStateOf(false)
    }
    var plannedActivityId:Int? by remember {
        mutableStateOf(null)
    }
    var idActivityType:Int? by remember {
        mutableStateOf(null)
    }
    var selectedActivity: HomeCareActivity? by remember {
        mutableStateOf(null)
    }
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
                text = {
                    Text(
                        text = stringResource(id = R.string.new_care),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "") },
                contentColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (trackerUiState.isEnabled) {
                        showSelectNewActivityPopup = true
                    }else{
                        showStartTrackingPopup = true
                    }

                })
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            HorizontalDivider()
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, max = 155.dp)
                    .background(MaterialTheme.colorScheme.surface)

            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    text = stringResource(id = R.string.opening),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = uiState.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PropertyList(
                    title = uiState.title,
                    properties = uiState.textItem
                )
            }
            HorizontalDivider()
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.cares),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 124.dp),
                content = {
                items(uiState.cares){ carePlanItem ->
                    CarePlanCoursesListItemView(item = carePlanItem, onClick = {

                            selectedActivity = it
                            showCreateCarePopup = true

                    })
                    Divider()
                }

            })
        }
    }
    if(showStartTrackingPopup){
        StartTrackerDialog(onDismissRequest = {
            if (it){
                trackerViewModel.startTracker() {
                    showSelectNewActivityPopup = true
                }
            }
            showStartTrackingPopup = false
        })
    }

    if (uiState.errorMessage != null){
        ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
            //viewModel.clearError()
        })
    }
    if(showSelectNewActivityPopup){
        SelectCareActivityPopupScreen(caseCode = viewModel.patientCod, planId = viewModel.planId.toInt(), onDismissRequest = { params ->
            showSelectNewActivityPopup = false
            if (params != null) {
                if (params.first) {
                    plannedActivityId = params.second.id
                }
                if (!params.first) {
                    idActivityType = params.second.id
                }
                showCreateCarePopup = true
            }else{
                viewModel.downloadData()
            }
        })
    }
    if (showCreateCarePopup){
        CreateEditCareScreen(homeCareActivity = selectedActivity, codCase = viewModel.patientCod, plannedActivityId = plannedActivityId, idActivityType = idActivityType, carePlanId = viewModel.planId.toIntOrNull(), onDismissRequest = {
            showCreateCarePopup = false
            plannedActivityId = null
            idActivityType = null
            selectedActivity = null
            if (it){
                viewModel.downloadData()
            }
        })
    }
}

@Composable
private fun PropertyList(title: String, properties: List<CarePlanDetailsUIState.TextItems>) {
    var showPropertyDialog by remember { mutableStateOf(false) }
    Row(modifier = Modifier
        .clickable {
            showPropertyDialog = true
        }
        .padding(16.dp)) {
        Column(
            modifier = Modifier.weight(1f)) {
            properties.forEach { property ->
                Text(
                    text = stringResource(id = property.titleStringId),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = property.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "")
    }
    if (showPropertyDialog) {
        PropertiesDialog(title = title, properties = properties) {
            showPropertyDialog = false
        }
    }

}

@Composable
@Preview
private fun PreviewCarePlanDetailsScreen() {
    AppTheme {
        CarePlanDetailsScreen() {}
    }
}