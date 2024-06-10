package it.airbagstudio.ticare.pages.vitalParameters.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import it.airbagstudio.ticare.pages.otherTreatments.search.TreatmentSearch
import it.airbagstudio.ticare.pages.vitalParameters.create.CreateNewVitalParameterSheet
import it.airbagstudio.ticare.pages.vitalParameters.search.VitalParameterSearchScreen
import it.airbagstudio.ticare.ui.components.BuildPageHeader
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getCompleteName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalParametersScreen(
    viewModel: VitalParametersScreenViewModel = hiltViewModel(),
    onBack: () -> Unit
){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearchBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = uiState.patient?.getCompleteName() ?: "") {
                onBack()
            }
        },
        floatingActionButton = {
            if (viewModel.canWrite) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .padding(start = 32.dp, bottom = 24.dp)
                        .fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.primary,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add, contentDescription = stringResource(
                                id = R.string.add_vital_parameter
                            )
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(
                                id = R.string.add_vital_parameter
                            )
                        )
                    },
                    onClick = {
                        showSearchBottomSheet = true
                    })
            }
        }
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            BuildPageHeader(
                title = viewModel.title,
                date = uiState.date?.format("dd/MM/yyyy") ?: "",
                shiftName = uiState.shift?.name ?: stringResource(id = R.string.all)
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 124.dp),
                content = {
                items(uiState.items){
                    VitalParameterItemView(item = it) {
                        viewModel.selectedTask = it.item
                    }
                }
            })
        }
    }
    if (showSearchBottomSheet) {
        VitalParameterSearchScreen(state = sheetState, onDismissRequest = {
            showSearchBottomSheet = false
            viewModel.selectedVitalSignCode = it
        })
    }
    if (uiState.error != null){
        ErrorAlert(message = uiState.error!!, onDismissRequest = {
            viewModel.clearError()
        })
    }
    if (viewModel.selectedTask != null){
        CreateNewVitalParameterSheet(task = viewModel.selectedTask, canWrite = viewModel.canWrite, onDismissRequest = { success ->
            viewModel.selectedVitalSignCode = null
            viewModel.selectedTask = null
            if (success) {
                viewModel.downloadData()
            }
        })
    }
    if (viewModel.selectedVitalSignCode != null){
        CreateNewVitalParameterSheet(vitalSignCode = viewModel.selectedVitalSignCode, caseCode = viewModel.patientCod, canWrite = viewModel.canWrite, onDismissRequest = { success ->
            viewModel.selectedVitalSignCode = null
            if (success) {
                viewModel.downloadData()
            }
        })
    }

}