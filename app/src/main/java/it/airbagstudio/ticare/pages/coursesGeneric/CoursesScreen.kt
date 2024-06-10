package it.airbagstudio.ticare.pages.coursesGeneric

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.HomeCareCourse
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.coursesGeneric.createEdit.CourseCreateEditScreen
import it.airbagstudio.ticare.utils.getCreateLabelId
import it.airbagstudio.ticare.utils.getLabelId
import it.airbagstudio.ticare.pages.nursingCourses.NursingCoursesItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = hiltViewModel(),
    onBack: () -> Unit
){
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.downloadData()
            }
            else -> {}
        }
    }
    var showCreateBottomSheet by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTasks by remember {
        mutableStateOf<HomeCareCourse?>(null)
    }
    Scaffold(
        floatingActionButton = {
            if (viewModel.canWrite) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .padding(start = 24.dp, bottom = 24.dp)
                        .fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        showCreateBottomSheet = true
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add, contentDescription = stringResource(
                                id = uiState.courseType.getLabelId()
                            )
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(
                                id = uiState.courseType.getCreateLabelId()
                            )
                        )
                    }
                )
            }
        },
        topBar = {
            ToolbarWithBackAndSync(title = uiState.caseName) {
                onBack()
            }
        }

    ) { values ->

        Column(modifier = Modifier.padding(values)) {
            Text(
                text = stringResource(id = uiState.courseType.getLabelId()),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${uiState.date} - ${uiState.shift ?: stringResource(id = R.string.all)}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )



            if (uiState.isLoading) {
                repeat(8) {
                    NursingCoursesItemViewLoading()
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 124.dp),
                    content = {
                        items(uiState.coursesList) { course ->
                            CourseListItemView(item = course) {
                                selectedTasks = course.course
                            }
                        }
                    })


            }
        }
        if(showCreateBottomSheet) {
            CourseCreateEditScreen(
                patientCode = viewModel.patientCode,
                courseType = viewModel.courseTypeName,
                course = null,
                canWrite = viewModel.canWrite
            ) {
                showCreateBottomSheet = false
                if (it){
                    viewModel.downloadData()
                }
            }
        }
        if (selectedTasks != null){
            CourseCreateEditScreen(
                patientCode = viewModel.patientCode,
                courseType = viewModel.courseTypeName,
                course = selectedTasks,
                canWrite = viewModel.canWrite
            ) {
                selectedTasks = null
                viewModel.downloadData()
            }
        }
    }
}