package it.airbagstudio.ticare.pages.carePlans.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

        // Tutto il piano vive in un unico scroll: niente anteprima compressa né dialog (TS1-6)
        LazyColumn(
            modifier = Modifier.padding(values),
            contentPadding = PaddingValues(bottom = 124.dp)
        ) {
            item {
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
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.opening),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            items(uiState.sections) { section ->
                CarePlanSectionView(section = section)
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 20.dp))
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(id = R.string.cares),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(uiState.cares) { carePlanItem ->
                CarePlanCoursesListItemView(item = carePlanItem, onClick = {
                    selectedActivity = it
                    showCreateCarePopup = true
                })
                Divider()
            }
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

/**
 * Una sezione del riepilogo. Il divider e i 20/16dp che lo circondano rendono lo stacco
 * fra sezioni sempre maggiore di quello fra le voci interne, che è il motivo per cui
 * prima le sezioni si fondevano fra loro. Vale anche per la prima sezione, che altrimenti
 * resterebbe incollata al blocco della data di apertura.
 */
@Composable
private fun CarePlanSectionView(section: CarePlanSection) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            when (section) {
                is CarePlanSection.Text -> {
                    SectionHeader(titleId = section.titleId)
                    Text(
                        text = section.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                is CarePlanSection.Bullets -> {
                    SectionHeader(titleId = section.titleId)
                    section.items.forEachIndexed { index, item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (index < section.items.lastIndex) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                    section.note?.let { AnnotationBox(note = it) }
                }

                is CarePlanSection.CodedItems -> {
                    SectionHeader(titleId = section.titleId, count = section.items.size)
                    section.items.forEachIndexed { index, item ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CodeChip(code = item.code)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                item.scale?.let { scale ->
                                    Text(
                                        modifier = Modifier.padding(top = 2.dp),
                                        text = stringResource(id = R.string.noc_scale, scale),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        if (index < section.items.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                is CarePlanSection.Planned -> {
                    SectionHeader(titleId = section.titleId, count = section.items.size)
                    section.items.forEachIndexed { index, activity ->
                        Text(
                            text = activity.title,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        activity.meta.forEach { meta ->
                            Text(
                                modifier = Modifier.padding(top = 2.dp),
                                text = meta,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (index < section.items.lastIndex) {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(titleId: Int, count: Int? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
        if (count != null) {
            Text(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 1.dp),
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun CodeChip(code: String) {
    Text(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        text = code,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1
    )
}

@Composable
private fun AnnotationBox(note: String) {
    Text(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = note,
        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
@Preview
private fun PreviewCarePlanDetailsScreen() {
    AppTheme {
        CarePlanDetailsScreen() {}
    }
}