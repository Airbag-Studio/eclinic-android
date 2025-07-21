package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel
import it.airbagstudio.ticare.ui.components.timeTracker.TravelTimeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCareActivityPopupScreen(
    caseCode: String,
    planId:Int,
    viewModel: SelectCareActivityPopupScreenViewModel = hiltViewModel(),
    trackerViewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current),
    onDismissRequest: (Pair<Boolean,Int>?) -> Unit
) {
    var showExecuteAllAlert by remember { mutableStateOf(false) }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismissRequest(null) },
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val trackerViewUIState by trackerViewModel.uiState.collectAsStateWithLifecycle()
        var showTravelTimeDialog by remember {
            mutableStateOf(false)
        }
        var tabIndex by remember {
            mutableIntStateOf(0)
        }
        LaunchedEffect(Unit) {
            viewModel.setCarePlanId(planId)
            viewModel.setPatientCode(caseCode)
            trackerViewModel.updateLastMinutesFromLastActivity()
        }
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.new_care))
                        }

                    },
                    actions = {
                        IconButton(onClick = { onDismissRequest(null) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (tabIndex == 0) {
                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        expanded = true,
                        text = {
                            Text(
                                text = stringResource(id = R.string.execute_all),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "") },
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            showExecuteAllAlert = true

                        })
                }
            },
            floatingActionButtonPosition = FabPosition.Center) { values ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(values)

            ) {

                val labels = listOf(
                    stringResource(id = R.string.planned),
                    stringResource(id = R.string.not_planned)
                )
                TabRow(
                    selectedTabIndex = tabIndex,
                    indicator = { tabPositions ->
                        if (tabIndex < tabPositions.size) {
                            SecondaryIndicator(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[tabIndex]),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                ) {
                    labels.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = {
                                tabIndex = index
                            },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        )
                    }
                }
                when (tabIndex){
                    0 -> {
                        ItemsList(uiState.plannedActivities){ id,_ ->
                            onDismissRequest(Pair(true,id))
                        }
                    }
                    1 -> {
                        SearchableList(uiState.query,uiState.unplannedActivities, onItemClick = { id,isTracking ->
                            if (isTracking){
                                showTravelTimeDialog = true
                            }else{
                                onDismissRequest(Pair(false,id))
                            }
                        }){
                            viewModel.setQuery(it)
                        }
                    }
                }
            }
        }
        if (showTravelTimeDialog){
            TravelTimeDialog(travelTime = trackerViewUIState.elapsedTimeFromLastActivity, onDismissRequest = { confirm ->
                if (confirm){
                    viewModel.sendTransferActivity(trackerViewUIState.elapsedTimeFromLastActivity){
                        showTravelTimeDialog = false
                    }
                }else{
                    showTravelTimeDialog = false
                }


            })
        }
        if (uiState.errorMessage != null){
            ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
                viewModel.clearErrors()
            })
        }
        /*
        if (!trackerViewUIState.isEnabled){
            onDismissRequest(null)
        }

         */

        if (showExecuteAllAlert) {
            AlertDialog(
                onDismissRequest = {
                    showExecuteAllAlert = false
                },
                title = { Text(text = stringResource(id = R.string.execute_all_activities_confirm_dialog_title)) },
                text = { Text(text = stringResource(id = R.string.execute_all_activities_confirm_dialog_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExecuteAllAlert = false
                            viewModel.executeAllPlannedActivities(trackerViewUIState.elapsedTimeFromLastActivity){
                                onDismissRequest(null)
                            }
                        }) {
                        Text("Ok")
                    }
                },
                dismissButton =
                    {
                        TextButton(
                            onClick = {
                                showExecuteAllAlert = false
                            }) {
                            Text(stringResource(id = R.string.cancel))
                        }

                    })

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableList(query:String,activities: List<SelectCareActivityPopupUIState.ActivityListItem>,onItemClick:(Int,Boolean)->Unit,onQueryChange: (String) -> Unit){
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Column {
        TextField(
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .height(56.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                focusRequester.freeFocus()
                focusManager.clearFocus(true)
            }),
            placeholder = {
                Text(text = stringResource(id = R.string.search))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            value = query,
            onValueChange = {
                onQueryChange(it)
            }
        )
        ItemsList(activities,onItemClick)
    }
}

@Composable
private fun ItemsList(activities: List<SelectCareActivityPopupUIState.ActivityListItem>,onItemClick:(Int,Boolean)->Unit){
    LazyColumn(
        content = {
            items(activities){
                val title = if (it.code != null){
                    "${it.code} - ${it.title}"
                }else{
                    it.title
                }
                ActivityListItemView(title,isTransferRow = it.isTransferActivity){
                    onItemClick(it.id,it.isTransferActivity)
                }
            }
        })
}
