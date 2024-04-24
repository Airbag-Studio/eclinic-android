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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.ToolTag
import it.airbagstudio.ticare.R
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
                LazyColumn(content = {

                    items(uiState.items.keys.toList()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                            text = it
                        )
                        val items = uiState.items.get(it)
                        items?.forEach { item ->
                            if (item.entityName == "VitalSignTask") {
                                DiaryVitaLParameterItemView(
                                    title = item.typeLbl ?: "",
                                    value = item.value ?: "",
                                    time = item.time,
                                    note = item.taskNotes ?: "",
                                    notExecuted = item.isSkipped ?: false,
                                )
                            } else if (item.entityName == "PharmacologicalTask") {
                                DiaryDrugAdministrationItemView(
                                    title = item.typeLbl ?: "",
                                    quantity = item.actualQuantity ?: "",
                                    note = item.taskNotes ?: "",
                                    expectedQuantity = item.expQuantity ?: "",
                                    time = item.time,
                                    isConfirmed = true,
                                    isReserve = item.isReserve ?: false,
                                    notExecuted = item.isSkipped ?: false,
                                    rejected = item.isRejected ?: false
                                )
                            } else if (item.entityName == "HomeCareCourse"){
                                DiaryNursingCourseItemView(
                                    title = item.title ?: "",
                                    duration = if(item.duration != null) item.duration.toString() else "-",
                                    time = item.time,
                                    note = item.desc ?: "",
                                    isPlanned = item.isScheduledTask ?: true
                                )
                            } else if (item.entityName == "HomeCareServiceTask"){
                                DiaryCarePlaneItemView(
                                    title = item.typeLbl ?: "",
                                    description = item.schedulerLbl ?: "",
                                    isPlanned = item.isScheduledTask ?: true,
                                    time = item.time,
                                    note = item.taskNotes ?: "",
                                    duration = item.duration?.toString() ?: "-"
                                )
                            } else if (item.entityName == "Wound"){
                                DiaryWoundItemView(
                                    title = item.bodyPart ?: "",
                                    note = item.appearanceDescription ?: ""
                                )
                            }else{
                                val toolTag = ToolTag.valueOf(item.entityName)
                                GenericDiaryListItemView(
                                    iconId = toolTag.getDiaryIconId(),
                                    typeIdLabel = toolTag.getLabelId(),
                                    activityName = item.typeLbl ?: item.title,
                                    duration = item.duration?.toString() ?: "-",
                                    time = item.time,
                                    note = item.taskNotes,
                                    color = if (item.taskNotes != null) seed else Color(0xFFCF4500),
                                    notExecuted = item.isSkipped ?: false
                                )
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(start = if (items.lastOrNull() == item) 0.dp else 16.dp))
                        }
                    }
                })
            }


        }
    }
}