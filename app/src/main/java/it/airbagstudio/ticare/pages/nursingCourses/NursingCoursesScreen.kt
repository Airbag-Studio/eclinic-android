package it.airbagstudio.ticare.pages.nursingCourses

import android.text.format.DateFormat
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
import ch.ticare.eclinic.library.entity.HomeCareCourse
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.nursingCourses.create.CreateNursingCourseScreen
import it.airbagstudio.ticare.pages.nursingCourses.create.EditNursingCourseScreen
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.getCompleteName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursingCoursesScreen(
    viewModel: NursingCoursesScreenViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var selectedTasks by remember {
        mutableStateOf<HomeCareCourse?>(null)
    }
    val errorMessages = remember {
        mutableStateOf<List<Int>?>(null)
    }

    var showCreateBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        floatingActionButton = {
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
                            id = R.string.add_nursing_course
                        )
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = R.string.add_nursing_course
                        )
                    )
                }
            )
        },
        topBar = {
            ToolbarWithBackAndSync(title = viewModel.patient?.getCompleteName() ?: "") {
                onBack()
            }
        }

    ) { values ->

        Column(modifier = Modifier.padding(values)) {
            Text(
                text = stringResource(id = R.string.nursing_courses_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${
                    DateFormat.format(
                        "dd/MM/yyyy",
                        viewModel.date
                    )
                } - ${viewModel.shiftName}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )



        if (viewModel.isLoading) {
            repeat(8) {
                NursingCoursesItemViewLoading()
                Divider(modifier = Modifier.padding(start = 16.dp))
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            LazyColumn(content = {
                items(viewModel.tasks) { task ->
                    NursingAdministrationItemView(
                        name = task.userValue,
                        time = task.dateTime,
                        duration = task.duration,
                        description = task.desc,
                    ) {
                        selectedTasks = task
                    }
                }
            })


        }
    }

    }
    if (errorMessages.value?.isNotEmpty() == true) {
        ErrorAlert(message = errorMessages.value?.map { stringResource(id = it) }
            ?.joinToString("\n") ?: "", onDismissRequest = { errorMessages.value = null })
    }

    if(showCreateBottomSheet) {
        CreateNursingCourseScreen(
            patientCode = viewModel.patientCode,
            state = sheetState
        ) {
            showCreateBottomSheet = false
            viewModel.reloadTasks()
        }
    }
    if(selectedTasks != null) {
        EditNursingCourseScreen(
            patientCode = viewModel.patientCode,
            state = sheetState,
            homeCareCourse = selectedTasks
        ) {
            selectedTasks = null
            viewModel.reloadTasks()
        }
    }
}