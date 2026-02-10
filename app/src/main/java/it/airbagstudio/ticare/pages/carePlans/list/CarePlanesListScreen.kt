package it.airbagstudio.ticare.pages.carePlans.list

import android.net.Uri
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.HomeCareActivity
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.carePlans.create.CreateEditCareScreen
import it.airbagstudio.ticare.pages.carePlans.details.CarePlanCoursesListItemView
import it.airbagstudio.ticare.pages.carePlans.selectActivity.SelectCareActivityPopupScreen
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.components.timeTracker.StartTrackerDialog
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel

@Composable
fun CarePlanesListScreen(
    trackingViewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current),
    viewModel: CarePlanesListScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val trackingUiState by trackingViewModel.uiState.collectAsStateWithLifecycle()
    var showStartTrackingDialog by remember {
        mutableStateOf(false)
    }
    var selectedId by remember {
        mutableIntStateOf(0)
    }
    var selectedActivityPlanId:Int? by remember {
        mutableStateOf(null)
    }
    var showStartTrackingPopup by remember {
        mutableStateOf(false)
    }
    var showSelectNewActivityPopup by remember {
        mutableStateOf(false)
    }
    var showCreateCarePopup by remember {
        mutableStateOf(false)
    }
    var plannedActivityId:Int? by remember {
        mutableStateOf(null)
    }
    var idActivityType:Int? by remember {
        mutableStateOf(null)
    }

    LifecycleResumeEffect(Unit) {
        // Do something on resume or launch effect
        viewModel.checkModifiedIds()
        onPauseOrDispose {

        }
    }
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = uiState.patientName) {
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
                    if (trackingUiState.isEnabled) {
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
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.care_planes),
                style = MaterialTheme.typography.headlineSmall

            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            if (uiState.isLoading) {
                repeat(8) {
                    PatientListItemViewLoading()
                }
            } else {
                LazyColumn(content = {
                    items(uiState.items) {
                        CarePlanesListItemView(item = it, onClick = { id ->
                            navigationActions.navigateToCarePlanDetailsScreen(
                                Uri.encode(viewModel.patientCod),
                                id.toString()
                            )
                        })
                        HorizontalDivider()
                    }
                })
            }
        }
        if (uiState.errorMessage != null) {
            ErrorAlert(message = uiState.errorMessage ?: "", onDismissRequest = {
                viewModel.clearError()
            }, onRetry = {
                viewModel.downloadData()
            })
        }
        if (showStartTrackingDialog) {
            StartTrackerDialog(onDismissRequest = { confirm ->
                showStartTrackingDialog = false
                if (confirm) {
                    trackingViewModel.startTracker(){
                        navigationActions.navigateToCarePlanDetailsScreen(
                            Uri.encode(viewModel.patientCod),
                            selectedId.toString()
                        )
                    }

                }
            })
        }
        if(showStartTrackingPopup){
            StartTrackerDialog(onDismissRequest = {
                if (it){
                    trackingViewModel.startTracker() {
                        showSelectNewActivityPopup = true
                    }
                }
                showStartTrackingPopup = false
            })
        }
        if(showSelectNewActivityPopup){
            SelectCareActivityPopupScreen(caseCode = viewModel.patientCod, planId = null, onDismissRequest = { params ->
                showSelectNewActivityPopup = false
                if (params != null) {
                    if (params.first) {
                        plannedActivityId = params.second.id
                    }
                    if (!params.first) {
                        idActivityType = params.second.id
                    }
                    selectedActivityPlanId = params.second.carePlanId
                    showCreateCarePopup = true
                }else{
                    viewModel.downloadData()
                }
            })
        }
        if (showCreateCarePopup){
            CreateEditCareScreen(homeCareActivity = null, codCase = viewModel.patientCod, plannedActivityId = plannedActivityId, idActivityType = idActivityType, carePlanId = selectedActivityPlanId, onDismissRequest = {
                showCreateCarePopup = false
                plannedActivityId = null
                idActivityType = null
                selectedActivityPlanId = null
                if (it){
                    viewModel.downloadData()
                }
            })
        }
    }
}