package it.airbagstudio.ticare.pages.drugsAdministration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration.EditDrugAdministrationSheet
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugsAdministrationScreen(
    viewModel: DrugsAdministrationScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
        floatingActionButton = {
            if (tabIndex == 0) {
                ExtendedFloatingActionButton(
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = { /*TODO*/ },
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
            ToolbarWithBackAndSync(title = "Nome paziente") {
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
                text = "21/08/2023 - Colazione",
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
                indicator = {tabPositions ->
                    TabRowDefaults.Indicator(
                        color = if(tabIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
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
                            color = if (index == tabIndex && index == 1){
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

            if(viewModel.isLoading){
                repeat(8) {
                    DrugAdministrationItemViewLoading()
                    Divider(modifier = Modifier.padding(start = 16.dp))
                }
            }else{
                when (tabIndex) {
                    0 -> {
                        DrugAdministrationItemView(name = "Meto Zeroch cpr ret 25mg", quantity = 3, time = "10:30", isCompleted = false){
                            showBottomSheet = true
                        }
                    }
                    1 -> {
                        DrugAdministrationItemView(name = "Meto Zeroch cpr ret 25mg", quantity = 3, time = "10:30", isCompleted = false, isReserve = true){
                            showBottomSheet = true
                        }
                    }
                }
            }


     
        }

    }
    if(showBottomSheet){
        EditDrugAdministrationSheet(isReserve = tabIndex == 1, state = sheetState) {
            showBottomSheet = false
        }
    }
}

@Composable
@Preview
private fun PreviewDrugsAdministrationScreen() {
    val viewModel = DrugsAdministrationScreenViewModel(SavedStateHandle.createHandle(null,null))
    viewModel.isLoading = false
    AppTheme() {
        DrugsAdministrationScreen(
            viewModel = viewModel,

            navigationActions = NavigationActions(NavController(LocalContext.current))) {

        }
    }
}