package it.airbagstudio.ticare.pages.tasksGeneric

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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.AgendaTask
import ch.ticare.eclinic.library.entity.TaskType
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.tasksGeneric.createEdit.TaskCreateEditScreen
import it.airbagstudio.ticare.pages.tasksGeneric.typeSelect.TypeSelectScreen
import it.airbagstudio.ticare.utils.getCreateLabelId
import it.airbagstudio.ticare.utils.getLabelId
import it.airbagstudio.ticare.ui.components.BuildPageHeader
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.canCreateNew

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    onBack: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        viewModel.downloadData()
    }
    var showSearchBottomSheet by remember {
        mutableStateOf(false)
    }
    var selectedType by remember {
        mutableStateOf<TaskType?>(null)
    }

    var taskToEdit by remember {
        mutableStateOf<AgendaTask?>(null)
    }

    var openCreateEditScreen by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = uiState.patientName) {
                onBack()
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (uiState.taskType.canCreateNew() && viewModel.canWrite) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.primary,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add, contentDescription = stringResource(
                                id = uiState.taskType.getCreateLabelId()
                            )
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(
                                id = uiState.taskType.getCreateLabelId()
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
            BuildPageHeader(title = stringResource(id = uiState.taskType.getLabelId()), date = uiState.selectedDate, shiftName = uiState.selectedShift?.name ?: stringResource(id = R.string.all))
            LazyColumn(
                contentPadding = PaddingValues(bottom = 124.dp),
                content = {
                    items(uiState.services){
                        TaskListItemView(item = it) {
                            taskToEdit = it.task
                            openCreateEditScreen = true
                        }
                    }
                })
        }
        if (showSearchBottomSheet){
            taskToEdit = null
            TypeSelectScreen(taskTypes = uiState.typesForTask) {
                showSearchBottomSheet = false
                it?.let { taskType ->
                    selectedType = taskType
                    openCreateEditScreen = true
                }
            }
        }
        if (openCreateEditScreen){
            TaskCreateEditScreen(
                taskType = selectedType,
                toolTag = uiState.taskType,
                patientCode = uiState.patientCode,
                taskToEdit = taskToEdit,
                canWrite = viewModel.canWrite
            ) {
                selectedType = null
                openCreateEditScreen = false
                taskToEdit = null
                if (it){
                    viewModel.downloadData()
                }
            }
        }
    }
}