package it.airbagstudio.ticare.pages.wounds.details

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.wounds.checks.create.CheckCreateDialogScreen
import it.airbagstudio.ticare.pages.wounds.common.ImagesDialog
import it.airbagstudio.ticare.pages.wounds.common.NotesDialog
import it.airbagstudio.ticare.pages.wounds.common.TitleValueView
import it.airbagstudio.ticare.pages.wounds.create.CreateWoundDialogScreen
import it.airbagstudio.ticare.ui.components.ConfirmWithNoteDialog
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.components.okHttpClient
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getPainter
import it.airbagstudio.ticare.utils.toDate
import java.time.Instant
import java.time.format.DateTimeFormatter

@Composable
fun WoundDetailsScreen(
    viewModel: WoundDetailsScreenViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
    onBack: () -> Unit
) {

    var showImagesDialog by remember {
        mutableStateOf(false)
    }
    var showNotesDialog by remember {
        mutableStateOf(false)
    }
    var showCheckCreateBottomSheet by remember {
        mutableStateOf(false)
    }
    var showCloseDialog by remember {
        mutableStateOf(false)
    }

    var showEditScreen by remember {
        mutableStateOf(false)
    }

    var lastCheckIdToEdit by remember {
        mutableStateOf<Int?>(null)
    }
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = viewModel.title) {
                onBack()
            }
        }
    ) {
        if (viewModel.errorMessage != null) {
            ErrorAlert(
                message = viewModel.errorMessage!!,
                onDismissRequest = { viewModel.errorMessage = null })
        }
        if (viewModel.wound != null) {
            Box(Modifier.padding(it)) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            text = stringResource(id = R.string.wound_type),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = viewModel.wound?.woundType ?: "",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        TitleValueView(
                            modifier = Modifier.weight(1f),
                            title = stringResource(id = R.string.origin),
                            value = viewModel.wound?.woundOrigin ?: ""
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxHeight()//fill the max height
                                .width(1.dp)
                        )
                        TitleValueView(
                            modifier = Modifier.weight(1f),
                            title = stringResource(id = R.string.opening),
                            value = viewModel.wound?.appearanceDate?.toDate("dd.MM.yyyy")
                                ?.format("dd/MM/yyyy") ?: ""
                        )
                    }
                    HorizontalDivider()
                    TitleValueView(
                        title = stringResource(id = R.string.position),
                        value = viewModel.wound?.parts?.map { it.name }?.joinToString(", ") ?: ""
                    )
                    HorizontalDivider()
                    TitleValueView(
                        title = stringResource(id = R.string.dimensions),
                        value = stringResource(
                            id = R.string.wound_size,
                            viewModel.wound?.length ?: 0,
                            viewModel.wound?.width ?: 0,
                            viewModel.wound?.depth ?: 0
                        )
                    )
                    HorizontalDivider()
                    TitleValueView(
                        title = stringResource(id = R.string.description),
                        value = viewModel.wound?.appearanceDescription ?: "",
                        singleLine = true,
                        onClick = {
                            showNotesDialog = true
                        }
                    )
                    HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                showImagesDialog = true
                            }
                            .padding(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_photos),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "${viewModel.wound?.photos?.count { it.iDCheck < 0 } ?: 0}")
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = ""
                        )
                    }
                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(id = R.string.controls),
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (viewModel.canWrite) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = seed
                                    ),
                                    onClick = { showCheckCreateBottomSheet = true }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                                    Text(text = stringResource(id = R.string.new_control))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.wound?.checks.isNullOrEmpty()) {
                            Text(text = stringResource(id = R.string.no_controls))
                        } else {
                            val controls = viewModel.wound?.checks?.map { check ->
                                ControlListItem(
                                    id = check.iD,
                                    date = check.dateTime.toDate("dd.MM.yyyy HH:mm")
                                        ?.format("dd/MM/yyyy, HH:mm")
                                        ?: "",
                                    description = check.medicationType,
                                    imagesCount = viewModel.wound?.photos?.count { it.iDCheck == check.iD }
                                        ?: 0,
                                    hasDataToUpload = viewModel.modifiedIds.contains(check.iD.toString())
                                )
                            } ?: listOf()
                            Column(
                                content = {
                                    controls.forEach{
                                        ControlListItemView(item = it) {
                                            navigationActions.navigateToCheckDetails(
                                                Uri.encode(viewModel.patientCod),
                                                viewModel.woundId,
                                                it
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                })
                        }


                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = { showCloseDialog = true },
                        enabled = viewModel.canWrite
                    ) {
                        Text(text = stringResource(id = R.string.close_wound))
                    }

                    TextButton(enabled = viewModel.canWrite, onClick = {
                        if (viewModel.wound?.checks?.isNullOrEmpty() == true) {
                            showEditScreen = true
                        }else{
                            lastCheckIdToEdit = viewModel.wound?.checks?.lastOrNull()?.iD
                            showCheckCreateBottomSheet = true
                        }
                    }) {
                        Text(text = stringResource(id = R.string.edit))
                    }
                }
            }

        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }

        }
    }

    if (showEditScreen){
        CreateWoundDialogScreen(codCase = viewModel.patientCod, woundId = viewModel.woundId.toInt(), canWrite = viewModel.canWrite, onDismissRequest = {
            showEditScreen = false
            if (it){
                viewModel.reloadWound()
            }
        })
    }

    if (showImagesDialog) {
        val imageLoader = ImageLoader.Builder(LocalContext.current)
            .okHttpClient(okHttpClient)
            .build()
        val woundDate = viewModel.wound?.appearanceDate?.toDate("dd.MM.yyyy")
            ?.format("dd/MM/yyyy") ?: ""
        val painters = viewModel.wound?.photos?.filter { it.iDCheck < 0 }?.map {
            it.getPainter(requestData = viewModel.requestImageRequestData, imageLoader = imageLoader, authTimestampHeader = DateTimeFormatter.ISO_INSTANT.format(
                Instant.now()), isOnline = viewModel.isOnline)
        } ?: listOf()
        ImagesDialog(
            date = woundDate,
            painters = painters
        ) {
            showImagesDialog = false
        }
    }

    if (showNotesDialog) {
        NotesDialog(
            title = stringResource(id = R.string.description),
            notes = viewModel.wound?.appearanceDescription ?: ""
        ) {
            showNotesDialog = false
        }
    }
    if (showCheckCreateBottomSheet) {
        CheckCreateDialogScreen(
            codCase = viewModel.patientCod,
            idWound = viewModel.woundId.toInt(),
            idGender = viewModel.genderId,
            idCheck = lastCheckIdToEdit,
            onDismissRequest = { success ->
                lastCheckIdToEdit = null
                if (success) viewModel.reloadWound()
                showCheckCreateBottomSheet = false
            })
    }
    if (showCloseDialog) {
        ConfirmWithNoteDialog(
            title = stringResource(id = R.string.closing_protocol),
            body = stringResource(id = R.string.wound_closing_note),
            onDismissRequest = { confirm, message ->
                if (confirm && message != null) {
                    viewModel.closeWound(message)
                }
                showCloseDialog = false
        })
    }
    if (viewModel.closedWound) {
        viewModel.closedWound = false
        onBack()
    }
}


@Composable
@Preview
private fun PreviewWoundDetails() {
    AppTheme {
        /*
        WoundDetailsScreen {

        }

         */
    }
}