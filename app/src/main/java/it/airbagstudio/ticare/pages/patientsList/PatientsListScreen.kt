package it.airbagstudio.ticare.pages.patientsList

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.Microzone
import ch.ticare.eclinic.library.entity.Zone
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.patientsList.downloadPatientData.SelectPatientsDialogScreen
import it.airbagstudio.ticare.pages.patientsList.offlineDataSheet.OfflineDataSheet
import it.airbagstudio.ticare.pages.patientsList.syncDataSheet.SyncDataSheetView
import it.airbagstudio.ticare.ui.components.DropDownButton
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopup
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.components.OfflineSyncImage
import it.airbagstudio.ticare.ui.components.PatientImage
import it.airbagstudio.ticare.ui.components.PatientListItemView
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithSyncAndSettings
import it.airbagstudio.ticare.utils.removePendingNotifications
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    viewModel: PatientListScreenViewModel = hiltViewModel(),
    navActions: NavigationActions

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchActive by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var showZonesPopup by remember {
        mutableStateOf(false)
    }
    var showMicrozonesPopup by remember {
        mutableStateOf(false)
    }
    var showDownloadPatientDataPopup by remember {
        mutableStateOf(false)
    }

    var openSyncSheet by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        // Do something on resume or launch effect
        viewModel.updatePatients()
        onPauseOrDispose {

        }
    }

    val isOfflineDataSheetVisible = !uiState.isOnline || (uiState.downloadCount > 0 && uiState.expireDate != null)
    val sheetState = rememberModalBottomSheetState()

    BottomSheetScaffold(
        scaffoldState = BottomSheetScaffoldState(sheetState, snackbarHostState = SnackbarHostState()),
        modifier = Modifier.consumeWindowInsets(
            WindowInsets.systemBars.only(WindowInsetsSides.Vertical)
        ),
        topBar = {
            ToolbarWithSyncAndSettings(
                title = uiState.companyName,
                isOnline = uiState.isOnline,
                onDownloadPatientDataClick = {
                    showDownloadPatientDataPopup = true
                },
                onSettingsClick = {
                    navActions.navigateToSettings()
                }
            )
        },
        sheetPeekHeight = if(isOfflineDataSheetVisible) 100.dp else 0.dp,
        sheetContent = {
            OfflineDataSheet(
                itemsToSync = uiState.modifiedCount,
                localItems = uiState.downloadCount,
                isOffline = !uiState.isOnline,
                expireDate = uiState.expireDate
            ){
                scope.launch {
                    sheetState.partialExpand()
                }
                if (uiState.isOnline){
                    viewModel.setOffline()
                }else{
                    if (viewModel.shouldUploadData){
                        openSyncSheet = true
                    }else{
                        viewModel.syncOfflineData()
                        removePendingNotifications(context)
                    }
                }
            }
        }

    ) { values ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(values)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                DockedSearchBar(
                    enabled = !viewModel.isLoading,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    placeholder = {
                        Text(text = stringResource(id = R.string.search))
                    },
                    query = viewModel.query,
                    onQueryChange = {
                        viewModel.query = it
                    },
                    onSearch = {
                        searchActive = false
                    },
                    active = searchActive,
                    onActiveChange = {
                        searchActive = it
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    },
                    trailingIcon = {
                        if (searchActive) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.search),
                                modifier = Modifier.clickable {
                                    viewModel.query = ""
                                    searchActive = false
                                }
                            )
                        }
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        if (viewModel.query.count() > 3) {
                            val filtered = uiState.caseList.filter {
                                it.completeName.contains(
                                    viewModel.query,
                                    ignoreCase = true
                                )
                            }
                            items(filtered) {
                                ListItem(
                                    headlineContent = { Text(it.completeName) },
                                    supportingContent = { Text(it.birthDate) },
                                    leadingContent = {
                                        PatientImage(
                                            it.patientCode,
                                            it.photo ?: "",
                                            viewModel.requestImageRequestData
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        //.padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clickable {
                                            //viewModel.query = ""
                                            //searchActive = false
                                            navActions.navigateToPatientDetails(Uri.encode(it.patientCode))
                                        }
                                )
                            }
                        }
                    }
                }
                Column(modifier = Modifier.padding(top = 70.dp)) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        DropDownButton(
                            modifier = Modifier.weight(1f),
                            value = uiState.selectedZone?.name
                                ?: stringResource(id = R.string.zones),
                            isEnabled = uiState.isOnline && !viewModel.isLoading && uiState.isRequestAllCasesAccessOn
                        ) {

                            showZonesPopup = true


                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        DropDownButton(
                            modifier = Modifier.weight(1f),
                            value = uiState.selectedMicrozone?.name
                                ?: stringResource(id = R.string.micro_zones),
                            isEnabled = uiState.isOnline && !viewModel.isLoading && uiState.selectedZone != null
                        ) {
                            showMicrozonesPopup = true
                        }
                    }
                    if (viewModel.isLoading) {
                        repeat(8) {
                            PatientListItemViewLoading()
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(uiState.caseList) { patientListItem ->
                                PatientListItemView(
                                    patient = patientListItem,
                                    viewModel.requestImageRequestData
                                ) {
                                    navActions.navigateToPatientDetails(Uri.encode(patientListItem.patientCode))
                                }
                            }
                        }
                    }
                    if (showZonesPopup) {
                        val visibleZones = if (uiState.isRequestAllCasesAccessOn) {
                            listOf(
                                ListPopupItem<Zone>(
                                    stringResource(id = R.string.all), null
                                )
                            ) + uiState.zones.map { ListPopupItem(label = it.name, it) }
                        } else {
                            uiState.userZones.map { ListPopupItem(label = it.name, it) }
                        }

                        ListPopup(
                            title = stringResource(id = R.string.zones),
                            items = visibleZones,
                            setShowDialog = {
                                showZonesPopup = it
                            },
                            onItemSelected = {
                                viewModel.setSelectedZone(it.item)
                                showZonesPopup = false
                            })
                    }
                    if (showMicrozonesPopup) {
                        ListPopup(title = stringResource(id = R.string.zones),
                            items = listOf(
                                ListPopupItem<Microzone>(
                                    stringResource(id = R.string.all), null
                                )
                            ) + uiState.microZones.map { ListPopupItem(label = it.name, it) },
                            setShowDialog = {
                                showMicrozonesPopup = it
                            },
                            onItemSelected = {
                                viewModel.setSelectedMicrozone(it.item)
                                showMicrozonesPopup = false
                            })
                    }
                }

            }
            Spacer(modifier = Modifier.weight(1f))
        }
        if (viewModel.errorMessage != null) {
            ErrorAlert(
                message = viewModel.errorMessage!!,
                onDismissRequest = { viewModel.errorMessage = null },
                onRetry = {
                    viewModel.errorMessage = null
                    viewModel.downloadData()
                })
        }
        if (showDownloadPatientDataPopup) {
            SelectPatientsDialogScreen(cases = uiState.caseList) {
                showDownloadPatientDataPopup = false
                viewModel.updatePatients()
            }
        }
        if (openSyncSheet){
            SyncDataSheetView(caseList = uiState.caseList) { success ->
                openSyncSheet = false
                viewModel.updatePatients()
            }
        }
    }
}