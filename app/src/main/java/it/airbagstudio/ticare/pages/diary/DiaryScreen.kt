package it.airbagstudio.ticare.pages.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.ToolTag
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.diary.allItemsTab.DiaryAllItemsTabContent
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentItemView
import it.airbagstudio.ticare.ui.components.BuildPageHeader
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getDiaryIconId
import it.airbagstudio.ticare.utils.getLabelId

@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember {
        mutableIntStateOf(0)
    }
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
                text = stringResource(id = R.string.diary),
                style = MaterialTheme.typography.headlineSmall

            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading) {
                repeat(8) {
                    PatientListItemViewLoading()
                }
            } else {
                TabRow(currentTab) {
                    Tab(selected = currentTab == 0, onClick = {
                        currentTab = 0
                    }) {
                        Text( modifier = Modifier.padding(vertical = 4.dp),text = stringResource(R.string.all_diary_items))
                    }
                    Tab(selected = currentTab == 1, onClick = {
                        currentTab = 1
                    }) {
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = stringResource(R.string.care_planes_diary_items))
                    }
                }
                if (currentTab == 0) {
                    DiaryAllItemsTabContent(uiState.items,uiState.clinicType)
                }else{
                    DiaryAllItemsTabContent(uiState.homeCareItems,uiState.clinicType)

                }
            }


        }
    }
}