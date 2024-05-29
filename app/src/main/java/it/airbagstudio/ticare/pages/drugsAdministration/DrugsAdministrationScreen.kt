package it.airbagstudio.ticare.pages.drugsAdministration

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.ticare.eclinic.library.entity.AgendaTask
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration.EditDrugAdministrationSheet
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.getExecTime
import it.airbagstudio.ticare.utils.getExpectedTime
import it.airbagstudio.ticare.utils.printTime
import it.airbagstudio.ticare.utils.validated
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugsAdministrationScreen(
    viewModel: DrugsAdministrationScreenViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showExecuteAllAlert by remember { mutableStateOf(false) }
    var selectedTasks by remember {
        mutableStateOf<AgendaTask?>(null)
    }
    var errorMessages = remember {
        mutableStateOf<List<Int>?>(null)
    }

    Scaffold(
        floatingActionButton = {

            if (tabIndex == 0) {
                val isFabEnabled =
                    !viewModel.tasks.filter { it.execDate == null && it.validated() }.isEmpty()
                ExtendedFloatingActionButton(
                    modifier = Modifier.alpha(if (isFabEnabled) 1f else 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (isFabEnabled) {
                            showExecuteAllAlert = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check, contentDescription = stringResource(
                                id = R.string.execute_all
                            )
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(
                                id = R.string.execute_all
                            )
                        )
                    }
                )
            }
        },
        topBar = {
            ToolbarWithBackAndSync(title = viewModel.patient?.getCompleteName() ?: "") {
                onBack()
            }
        }

    ) { values ->

        Column(modifier = Modifier.padding(values)) {
            Text(
                text = stringResource(id = R.string.drug_administration),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
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
            val tabs = listOf(
                stringResource(id = R.string.prescriptions),
                stringResource(id = R.string.reserves)
            )

            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[tabIndex])
                            .padding(horizontal = 50.dp)
                            .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp)),
                        color = if (tabIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
                //contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = {
                        Text(
                            text = title,
                            color = if (index == tabIndex && index == 1) {
                                MaterialTheme.colorScheme.tertiary
                            } else if (index == tabIndex && index == 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            if (viewModel.isLoading) {
                repeat(8) {
                    DrugAdministrationItemViewLoading()
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                }
            } else {
                when (tabIndex) {
                    0 -> {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 124.dp),
                            content = {
                            items(viewModel.tasks) { task ->
                                DrugAdministrationItemView(
                                    name = task.itemDescription ?: "",
                                    quantity = if(task.getExecTime()?.printTime()!= null) task.quantity else task.expQuantity,
                                    time = task.getExecTime()?.printTime() ?: task.getExpectedTime()
                                        ?.printTime() ?: "",
                                    reserves = task.reservesCount,
                                    notExecuted = task.isSkipped,
                                    isConfirmed = task.validated(),
                                    rejected = task.rejected ?: false,
                                    isCompleted = task.execTime != null,
                                    hasDataToUpload = viewModel.modifiedIds.contains(task.pkey.toString())
                                ) {
                                    selectedTasks = task
                                    CoroutineScope(Dispatchers.Default).launch {
                                        delay(500)
                                        showBottomSheet = true
                                    }
                                }
                            }
                        })
                    }

                    1 -> {
                        LazyColumn(content = {
                            items(viewModel.reserves) { task ->
                                DrugAdministrationItemView(
                                    name = task.itemDescription ?: "",
                                    quantity = if(task.getExecTime()?.printTime()!= null) task.quantity else task.expQuantity,
                                    time = task.getExecTime()?.printTime() ?: task.getExpectedTime()
                                        ?.printTime() ?: "",
                                    isCompleted = task.execTime != null,
                                    isReserve = true,
                                    notExecuted = task.isSkipped,
                                    isConfirmed = task.validated(),
                                    rejected = task.rejected ?: false,
                                    hasDataToUpload = viewModel.modifiedIds.contains(task.pkey.toString())
                                ) {
                                    selectedTasks = task
                                    CoroutineScope(Dispatchers.Default).launch {
                                        delay(500)
                                        showBottomSheet = true
                                    }

                                }
                            }
                        })
                    }
                }
            }


        }

    }
    if (showBottomSheet) {
        EditDrugAdministrationSheet(
            task = selectedTasks
        ) {
            showBottomSheet = false
            viewModel.reloadTasks()
        }
    }

    if (showExecuteAllAlert) {
        AlertDialog(
            onDismissRequest = {
                showExecuteAllAlert = false
            },
            title = { Text(text = stringResource(id = R.string.execute_all_confirm_dialog_title)) },
            text = { Text(text = stringResource(id = R.string.execute_all_confirm_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExecuteAllAlert = false
                        viewModel.executeAll()
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
    if (errorMessages.value?.isNotEmpty() == true) {
        ErrorAlert(message = errorMessages.value?.map { stringResource(id = it) }
            ?.joinToString("\n") ?: "", onDismissRequest = { errorMessages.value = null })
    }
}