package it.airbagstudio.ticare.pages.otherTreatments

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.OtherService
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.otherTreatments.create.CreateTreatmentScreen
import it.airbagstudio.ticare.pages.otherTreatments.search.TreatmentSearch
import it.airbagstudio.ticare.ui.components.BuildPageHeader
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherTreatmentScreen(
    viewModel: OtherTreatmentScreenViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearchBottomSheet by remember { mutableStateOf(false) }
    var showCreateBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedArticleId = remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = uiState.patientName) {
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
                    showSearchBottomSheet = true
                })
        }
    ) { values ->

        Column(
            Modifier
                .padding(values)
        ) {
            BuildPageHeader(title = stringResource(id = R.string.other_prescriptions), date = uiState.selectedDate?.format("dd/MM/yyyy") ?: "", shiftName = uiState.selectedShift?.name ?: stringResource(id = R.string.all))
            LazyColumn(
                contentPadding = PaddingValues(bottom = 124.dp),
                content = {
                items(uiState.services){
                    OtherTreatmentItemView(item = it) {
                        viewModel.setSelectedServiceId(it.id)
                    }
                }
            })
        }
    }
    if (showSearchBottomSheet) {
        TreatmentSearch(state = sheetState) {
            showSearchBottomSheet = false
            it?.let { _selectedArticleId ->
                selectedArticleId.intValue = _selectedArticleId
                showCreateBottomSheet = true
            }
        }
    }
    if (showCreateBottomSheet){
        CreateTreatmentScreen(articleId = selectedArticleId.intValue, patientCode = viewModel.patientCod) {
            selectedArticleId.value = 0
            showCreateBottomSheet = false
            viewModel.downloadData()
        }
    }
    if (viewModel.selectedService != null){

        CreateTreatmentScreen(articleId = 0, patientCode = viewModel.patientCod, service = viewModel.selectedService) {
            viewModel.selectedService = null
            showCreateBottomSheet = false
            viewModel.downloadData()
        }
    }
}



@Composable
@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun PreviewOtherTreatmentScreen(){
    AppTheme {
        Scaffold {  values ->
            OtherTreatmentScreen {

            }
        }
    }
}