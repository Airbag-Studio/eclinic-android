package it.airbagstudio.ticare.pages.wounds.details

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.pages.wounds.checks.create.CheckCreateDialogScreen
import it.airbagstudio.ticare.pages.wounds.common.ImagesDialog
import it.airbagstudio.ticare.pages.wounds.common.NotesDialog
import it.airbagstudio.ticare.pages.wounds.common.TitleValueView
import it.airbagstudio.ticare.pages.wounds.create.CreateWoundDialogScreen
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.toDate

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
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = stringResource(id = R.string.wounds)) {
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
            Column(
                Modifier
                    .padding(it)
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
                Divider()
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    TitleValueView(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.origin),
                        value = viewModel.wound?.woundOrigin ?: ""
                    )
                    Divider(
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
                Divider()
                TitleValueView(
                    title = stringResource(id = R.string.position),
                    value = viewModel.wound?.parts?.map { it.name }?.joinToString(", ") ?: ""
                )
                Divider()
                TitleValueView(
                    title = stringResource(id = R.string.dimensions),
                    value = stringResource(
                        id = R.string.wound_size,
                        viewModel.wound?.length ?: 0,
                        viewModel.wound?.width ?: 0,
                        viewModel.wound?.depth ?: 0
                    )
                )
                Divider()
                TitleValueView(
                    title = stringResource(id = R.string.description),
                    value = viewModel.wound?.appearanceDescription ?: "",
                    singleLine = true,
                    onClick = {
                        showNotesDialog = true
                    }
                )
                Divider()
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
                Divider()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(id = R.string.controls),
                            style = MaterialTheme.typography.titleLarge
                        )
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
                    Spacer(modifier = Modifier.height(16.dp))
                    if (viewModel.wound?.checks.isNullOrEmpty()) {
                        Text(text = stringResource(id = R.string.no_controls))
                    } else {
                        val controls = viewModel.wound?.checks?.map { check ->
                            ControlListItem(
                                id = check.iD,
                                date = check.dateTime.toDate("dd.MM.yyyy HH:mm")
                                    ?.format("dd/MM/yyyy")
                                    ?: "",
                                description = check.medicationType,
                                imagesCount = viewModel.wound?.photos?.count { it.iDCheck == check.iD }
                                    ?: 0
                            )
                        }
                        controls?.forEach {
                            ControlListItemView(item = it) {
                                navigationActions.navigateToCheckDetails(
                                    Uri.encode(viewModel.patientCod),
                                    viewModel.woundId,
                                    it
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = seed
                        ),
                        border = BorderStroke(1.dp, seed),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /*TODO*/ }) {
                        Text(text = stringResource(id = R.string.close_wound))
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
    /*
    if (showImagesDialog) {
        val woundDate = viewModel.wound?.appearanceDate?.toDate("dd.MM.yyyy")
            ?.format("dd/MM/yyyy") ?: ""
        ImagesDialog(
            date = woundDate,
            photosIds = viewModel.wound?.photos?.filter { it.iDCheck < 0 }?.map { it.iD } ?: listOf(),
            requestData = viewModel.requestImageRequestData
        ) {
            showImagesDialog = false
        }
    }

     */
    if (showNotesDialog){
        NotesDialog(title = stringResource(id = R.string.description), notes = viewModel.wound?.appearanceDescription ?: "") {
            showNotesDialog = false
        }
    }
    if (showCheckCreateBottomSheet){
        CheckCreateDialogScreen(codCase = viewModel.patientCod, idWound = viewModel.woundId.toInt(), idGender = 1, onDismissRequest = {success ->
            showCheckCreateBottomSheet = false
        })
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