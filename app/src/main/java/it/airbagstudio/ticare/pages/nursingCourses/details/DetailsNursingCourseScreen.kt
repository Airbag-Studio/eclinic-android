package it.airbagstudio.ticare.pages.nursingCourses.details

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.HomeCareCourse
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNursingCourseScreen(
    viewModel: EditNursingCourseSheetViewModel = hiltViewModel(),
    patientCode: String,
    onDismissRequest: () -> Unit
) {

        LaunchedEffect(Unit) {
            run {
                viewModel.setScreenType(ScreenType.Add)
                viewModel.loadCategory(patientCode = patientCode)
                viewModel.setShowInDiary(true)
            }
        }
        BuildSheetContent(viewModel = viewModel, patientCode, onDismissRequest)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNursingCourseScreen(
    viewModel: EditNursingCourseSheetViewModel = hiltViewModel(),
    patientCode: String,
    homeCareCourse: HomeCareCourse?,
    onDismissRequest: () -> Unit,
) {

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
    ) {
        LaunchedEffect(Unit) {
            run {
                if (homeCareCourse != null) {
                    viewModel.setScreenType(ScreenType.Edit(homeCareCourse))
                } else {
                    viewModel.setScreenType(ScreenType.Add)
                }
                viewModel.loadCategory(patientCode)
            }
        }
        BuildSheetContent(viewModel = viewModel, patientCode, onDismissRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildSheetContent(
    viewModel: EditNursingCourseSheetViewModel,
    patientCode: String,
    onDismissRequest: () -> Unit
) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCategoryPopup by remember {
        mutableStateOf(false)
    }

    val column1Weight = 0.6f
    val column2Weight = 1 - column1Weight
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.new_nursing_course))
                    }

                },
                actions = {
                    IconButton(onClick = { onDismissRequest() }) {
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


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    modifier = Modifier
                        .clickable {
                            showCategoryPopup = true
                        }
                        .fillMaxWidth(),
                    maxLines = 1,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    value = uiState.newNursingCourse.courseCategoryType?.name
                        ?: stringResource(
                            id = R.string.no_category
                        ),
                    label = {
                        Text(text = stringResource(id = R.string.category))
                    },
                    onValueChange = {

                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.id_dropdown),
                            contentDescription = ""
                        )
                    },
                )

            }

            Spacer(modifier = Modifier.height(24.dp))
            Row {
                CalendarTextField(
                    modifier = Modifier.weight(column1Weight),
                    date = uiState.newNursingCourse.dateTime,
                    label = { Text(text = stringResource(id = R.string.actual_date_time)) },
                    onDateChanged = {
                        viewModel.setDate(it)
                    }
                )
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1,
                    modifier = Modifier.weight(column2Weight),
                    value = uiState.newNursingCourse.duration?.toString() ?: "",
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
                    .height((screenHeight / 3).toInt().dp)
                    .fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.description))
                },
                value = uiState.newNursingCourse.description ?: "", onValueChange = {
                    viewModel.setDescription(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            SwitchItem(
                label = stringResource(id = R.string.show_in_diary),
                enabled = true,
                value = uiState.newNursingCourse.showInDiary
            ) {
                viewModel.setShowInDiary(it)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                onClick = {
                    viewModel.saveButtonClick(patientCode = patientCode)
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

        if (uiState.error != null) {
            ErrorAlert(
                message = uiState.error!!,
                onDismissRequest = {
                    viewModel.clearState()
                })
        }
        if (showCategoryPopup) {
            ListPopup(
                title = stringResource(id = R.string.category),
                items = uiState.categoriesTypes.map {
                    ListPopupItem(
                        label = it.name,
                        item = it
                    )
                } + ListPopupItem(
                    stringResource(id = R.string.no_category), item = null
                ),
                setShowDialog = {
                    showCategoryPopup = false
                },
                onItemSelected = {
                    viewModel.setCategoryId(it.item?.id)
                    showCategoryPopup = false
                })
        }
        if (uiState.isSuccess) {
            viewModel.clearState()
            onDismissRequest()
        }
    }
}


@Composable
private fun SwitchItem(
    label: String,
    value: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label
        )
        Switch(
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            checked = value,
            enabled = enabled,
            onCheckedChange = {
                onChange(it)
            })
    }
}