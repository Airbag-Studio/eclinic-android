package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import it.airbagstudio.ticare.LocalActivity
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.FullScreenDialog
import it.airbagstudio.ticare.ui.components.timeTracker.TimeTrackerViewModel
import it.airbagstudio.ticare.ui.components.timeTracker.TravelTimeDialog
import it.airbagstudio.ticare.ui.theme.surface_container
import kotlinx.coroutines.awaitCancellation

private const val LOADING_PLACEHOLDERS_COUNT = 8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCareActivityPopupScreen(
    caseCode: String,
    planId: Int?,
    viewModel: SelectCareActivityPopupScreenViewModel = hiltViewModel(),
    trackerViewModel: TimeTrackerViewModel = hiltViewModel(LocalActivity.current),
    onDismissRequest: (Pair<Boolean, SelectCareActivityPopupUIState.ActivityListItem>?) -> Unit
) {
    var showExecuteAllAlert by remember { mutableStateOf(false) }
    val errorMessage = viewModel.errorMessage.collectAsStateWithLifecycle()
    FullScreenDialog(onDismissRequest = { onDismissRequest(null) }) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val trackerViewUIState by trackerViewModel.uiState.collectAsStateWithLifecycle()
        var showTravelTimeDialog by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(Unit) {
            viewModel.loadData(carePlanId = planId, patientCode = caseCode)
            trackerViewModel.updateLastMinutesFromLastActivity()
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.new_care))
                        }
                    },
                    // Le checkbox sono sempre visibili: non serve più una modalità "Seleziona"
                    actions = {
                        IconButton(onClick = { onDismissRequest(null) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (uiState.selectedCount > 0) {
                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        expanded = true,
                        text = {
                            Text(
                                text = stringResource(
                                    id = R.string.execute_selected,
                                    uiState.selectedCount
                                ),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "") },
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = { showExecuteAllAlert = true })
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { values ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                SearchField(query = uiState.query, onQueryChange = { viewModel.setQuery(it) })
                LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
                    plannedBlock(
                        uiState = uiState,
                        onToggleAll = { viewModel.togglePlannedSelection() },
                        onSelectedChange = { id, isSelected ->
                            viewModel.changeActivitySelection(id, isPlanned = true, isSelected = isSelected)
                        },
                        onItemClick = { item -> onDismissRequest(Pair(true, item)) }
                    )
                    unplannedBlock(
                        uiState = uiState,
                        onSelectedChange = { id, isSelected ->
                            viewModel.changeActivitySelection(id, isPlanned = false, isSelected = isSelected)
                        },
                        onItemClick = { item, isTransfer ->
                            if (isTransfer) {
                                showTravelTimeDialog = true
                            } else {
                                onDismissRequest(Pair(false, item))
                            }
                        }
                    )
                }
            }
        }

        if (showTravelTimeDialog) {
            TravelTimeDialog(
                travelTime = trackerViewUIState.elapsedTimeFromLastActivity,
                onDismissRequest = { confirm ->
                    if (confirm) {
                        viewModel.sendTransferActivity() {
                            showTravelTimeDialog = false
                        }
                    } else {
                        showTravelTimeDialog = false
                    }


                })
        }
        if (errorMessage.value != null) {
            ErrorAlert(message = errorMessage.value!!, onDismissRequest = {
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
                            viewModel.executeSelectedActivities {
                                trackerViewModel.updateLastMinutesFromLastActivity()
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
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    TextField(
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .height(56.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            focusRequester.freeFocus()
            focusManager.clearFocus(true)
        }),
        placeholder = {
            Text(text = stringResource(id = R.string.search_all))
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
        onValueChange = onQueryChange
    )
}

/**
 * Blocco delle prestazioni pianificate: card distinta con il "seleziona tutte" a tre stati.
 * È un unico item della lista perché la card deve avvolgere tutto il blocco; le pianificate
 * di un piano sono poche, quindi non serve la pigrizia della LazyColumn.
 */
private fun LazyListScope.plannedBlock(
    uiState: SelectCareActivityPopupUIState,
    onToggleAll: () -> Unit,
    onSelectedChange: (Int, Boolean) -> Unit,
    onItemClick: (SelectCareActivityPopupUIState.ActivityListItem) -> Unit
) {
    if (uiState.isLoadingPlanned) {
        item {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                SectionTitle(text = stringResource(id = R.string.planned))
                repeat(2) { ActivityListItemViewLoading() }
            }
        }
        return
    }
    // Nessuna prestazione pianificata: l'intero blocco sparisce, intestazione compresa
    if (uiState.plannedSections.isEmpty()) return

    item {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(surface_container)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleAll() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        id = R.string.planned_with_count,
                        uiState.plannedSections.sumOf { it.items.size }
                    )
                )
                Text(
                    text = stringResource(id = R.string.select_all_planned),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TriStateCheckbox(
                    state = when (uiState.plannedSelection) {
                        SelectCareActivityPopupUIState.PlannedSelection.ALL -> ToggleableState.On
                        SelectCareActivityPopupUIState.PlannedSelection.SOME -> ToggleableState.Indeterminate
                        SelectCareActivityPopupUIState.PlannedSelection.NONE -> ToggleableState.Off
                    },
                    onClick = onToggleAll
                )
            }
            uiState.plannedSections.forEach { section ->
                BillingModeSectionTitle(section)
                section.items.forEach { item ->
                    ActivityRow(
                        item = item,
                        onClick = { onItemClick(item) },
                        onSelectedChange = { isSelected -> onSelectedChange(item.id, isSelected) }
                    )
                }
            }
        }
    }
}

/** Blocco delle non pianificate: nessun "seleziona tutte", si spuntano una a una. */
private fun LazyListScope.unplannedBlock(
    uiState: SelectCareActivityPopupUIState,
    onSelectedChange: (Int, Boolean) -> Unit,
    onItemClick: (SelectCareActivityPopupUIState.ActivityListItem, Boolean) -> Unit
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(
                modifier = Modifier.weight(1f),
                text = stringResource(
                    id = R.string.unplanned_with_count,
                    uiState.unplannedSections.sumOf { it.items.size }
                )
            )
            Text(
                text = stringResource(id = R.string.single_selection_hint),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
    if (uiState.isLoadingUnplanned) {
        items(LOADING_PLACEHOLDERS_COUNT) { ActivityListItemViewLoading() }
        return
    }
    if (uiState.unplannedSections.isEmpty()) {
        item { NoResults() }
        return
    }
    uiState.unplannedSections.forEach { section ->
        item { BillingModeSectionTitle(section) }
        items(section.items) { item ->
            ActivityRow(
                item = item,
                onClick = { onItemClick(item, item.isTransferActivity) },
                onSelectedChange = { isSelected -> onSelectedChange(item.id, isSelected) }
            )
        }
    }
}

@Composable
private fun ActivityRow(
    item: SelectCareActivityPopupUIState.ActivityListItem,
    onClick: () -> Unit,
    onSelectedChange: (Boolean) -> Unit
) {
    val title = if (item.code != null) "${item.code} - ${item.title}" else item.title
    ActivityListItemView(
        isSelecting = true,
        title = title,
        isTransferRow = item.isTransferActivity,
        isSelected = item.isSelected,
        onClick = onClick,
        onSelectedChange = onSelectedChange
    )
}

@Composable
private fun BillingModeSectionTitle(section: SelectCareActivityPopupUIState.ActivitySection) {
    val title = section.title
    val titleRes = section.titleRes
    if (title != null) {
        BillingModeHeader(title = title)
    } else if (titleRes != null) {
        BillingModeHeader(title = stringResource(id = titleRes))
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun NoResults() {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        text = stringResource(id = R.string.no_results),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun BillingModeHeader(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
}
