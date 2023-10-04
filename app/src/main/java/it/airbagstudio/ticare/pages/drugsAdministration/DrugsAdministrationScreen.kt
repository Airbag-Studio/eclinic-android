package it.airbagstudio.ticare.pages.drugsAdministration

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import ch.ticare.eclinic.library.entity.AgendaPharmacologicalTask
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration.EditDrugAdministrationSheet
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.getCompleteName
import it.airbagstudio.ticare.utils.getExpectedTime
import it.airbagstudio.ticare.utils.printTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugsAdministrationScreen(
    viewModel: DrugsAdministrationScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showExecuteAllAlert by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTasks by remember {
        mutableStateOf<AgendaPharmacologicalTask?>(null)
    }
    Scaffold(
        floatingActionButton = {
            if (tabIndex == 0 && !viewModel.tasks.filter { it.execDate == null }.isEmpty()) {
                ExtendedFloatingActionButton(

                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        showExecuteAllAlert = true
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

            val contentColor = {

            }

            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = if (tabIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[tabIndex])
                            .padding(horizontal = 50.dp)
                            .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
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
                    Divider(modifier = Modifier.padding(start = 16.dp))
                }
            } else {
                when (tabIndex) {
                    0 -> {
                        LazyColumn(content = {
                            items(viewModel.tasks) { task ->
                                DrugAdministrationItemView(
                                    name = task.itemDescription,
                                    quantity = task.expQuantity,
                                    time = task.getExpectedTime().printTime(),
                                    isCompleted = task.execTime != null
                                ) {
                                    selectedTasks = task
                                    showBottomSheet = true

                                }
                            }
                        })


                    }

                    1 -> {
                        LazyColumn(content = {
                            items(viewModel.reserves) { task ->
                                DrugAdministrationItemView(
                                    name = task.entityName,
                                    quantity = task.expQuantity,
                                    time = task.getExpectedTime().printTime(),
                                    isCompleted = task.execTime != null,
                                    isReserve = true
                                ) {
                                    if (task.execDate == null) {
                                        selectedTasks = task
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
            state = sheetState,
            task = selectedTasks
        ) {
            showBottomSheet = false
            viewModel.reloadTasks()
        }
    }

    if (showExecuteAllAlert){
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
}