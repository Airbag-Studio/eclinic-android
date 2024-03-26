package it.airbagstudio.ticare.pages.coursesGeneric.createEdit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ch.ticare.eclinic.library.entity.HomeCareCourse
import ch.ticare.eclinic.library.entity.ToolTag
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.components.PopupTextField
import it.airbagstudio.ticare.ui.components.SwitchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCreateEditScreen(
    viewModel: CourseCreateEditScreenViewModel = hiltViewModel(),
    patientCode: String,
    courseType: String,
    course: HomeCareCourse?,
    onDismissRequest: (Boolean) -> Unit,
) {
    LaunchedEffect(key1 = Unit) {

        viewModel.patientCode = patientCode
        viewModel.courseType = ToolTag.valueOf(courseType)
        viewModel.downloadData()
        course?.let {
            viewModel.setCourse(course)
        }

    }



    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = viewModel.isSuccess) {
        if (viewModel.isSuccess.value){
            viewModel.clearState()
            onDismissRequest(true)
        }
    }
    val column1Weight = 0.6f
    val column2Weight = 1 - column1Weight
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismissRequest(false)
        },
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.new_nursing_course))
                        }

                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.clearState()
                            onDismissRequest(false)
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            }) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                PopupTextField(label = stringResource(id = R.string.category), value = uiState.course.selectedCategory?.name ?: "", items = uiState.categories.map { ListPopupItem(it.name,it) }) {
                    it.item?.let { it1 -> viewModel.setCategory(it1) }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    CalendarTextField(
                        modifier = Modifier.weight(column1Weight),
                        date = uiState.course.dateAndTime,
                        label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                        onDateChanged = {
                            viewModel.setDateAndTime(it)
                        }
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        maxLines = 1,
                        modifier = Modifier.weight(column2Weight),
                        value = "${uiState.course.duration}",
                        label = {
                            Text(text = stringResource(id = R.string.duration))
                        },
                        onValueChange = {
                            val duration = it.toIntOrNull()
                            if (it.isEmpty()) {
                                viewModel.setDuration(null)
                            } else {
                                viewModel.setDuration(duration)
                            }

                        })

                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .height((screenHeight - 500).dp)
                        .fillMaxWidth(),
                    label = {
                        Text(text = stringResource(id = R.string.description))
                    },
                    value = uiState.course.description, onValueChange = {
                        viewModel.setDescription(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                SwitchItem(
                    label = stringResource(id = R.string.show_in_diary),
                    enabled = true,
                    value = uiState.course.showInDiary
                ) {
                    viewModel.setShowInDiary(it)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    onClick = {
                        viewModel.saveCourse()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.execute)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.save))
                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        if (viewModel.errorMessage.value != null) {
            ErrorAlert(message = viewModel.errorMessage.value!!, onDismissRequest = {
                viewModel.errorMessage.value = null
            } )
        }
    }
}