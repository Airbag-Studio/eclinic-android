package it.airbagstudio.ticare.pages.wounds.checks.create

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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.wounds.common.AddingImagesGallery
import it.airbagstudio.ticare.pages.wounds.create.CreateWoundDialogScreen
import it.airbagstudio.ticare.pages.wounds.create.CreateWoundDialogScreenViewModel
import it.airbagstudio.ticare.ui.components.AddPhotoButton
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ListPopupItem
import it.airbagstudio.ticare.ui.components.MultiselectPopupTextField
import it.airbagstudio.ticare.ui.components.PopupTextField
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.rememberImeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckCreateDialogScreen(
    codCase: String,
    idWound: Int,
    idGender: Int,
    onDismissRequest: (Boolean) -> Unit,
    viewModel: CheckCreateDialogScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.clearData()
        viewModel.codCase = codCase
        viewModel.idWound = idWound
        viewModel.idGender = idGender
    }
    val scrollState = rememberScrollState()

    val imeState = rememberImeState()

    LaunchedEffect(key1 = imeState.value, block = {
        if (imeState.value){
            scrollState.scrollTo(scrollState.maxValue)
        }
    })


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(false)
        }) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.new_check))
                        }
                    },
                    actions = {
                        IconButton(onClick = { onDismissRequest(false) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            }) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                CalendarTextField(
                    modifier = Modifier.fillMaxWidth(),
                    date = uiState.check.date, label = {
                        Text(text = stringResource(id = R.string.date_and_time))
                    }, onDateChanged = {
                        viewModel.setDate(it)
                    })
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.surface),
                    value = uiState.check.woundArea ?: "",
                    items = viewModel.woundArea.value
                ){
                    viewModel.setWoundArea(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.depth),
                    value = uiState.check.woundDepth ?: "",
                    items = viewModel.woundDepth.value
                ){
                    viewModel.setWoundDepth(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.necrosis_zones),
                    value = uiState.check.woundNecrosis ?: "",
                    items = viewModel.woundNecrosis.value
                ){
                    viewModel.setWoundNecrosis(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.fibrin),
                    value = uiState.check.woundFibrin ?: "",
                    items = viewModel.woundFibrin.value
                ){
                    viewModel.setWoundFibrin(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.granulation_tissue),
                    value = uiState.check.woundGranulation ?: "",
                    items = viewModel.woundGranulation.value
                ){
                    viewModel.setWoundGranulation(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.smell),
                    value = uiState.check.woundSmell ?: "",
                    items = viewModel.woundSmell.value
                ){
                    viewModel.setWoundSmell(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.secretion),
                    value = uiState.check.woundSecretion ?: "",
                    items = viewModel.woundSecretion.value
                ){
                    viewModel.setWoundSecretion(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.surrounding_skin),
                    value = uiState.check.woundSurroundingSkin ?: "",
                    items = viewModel.woundSurroundingSkin.value
                ){
                    viewModel.setWoundSurroundingSkin(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.pain),
                    value = uiState.check.woundPainType ?: "",
                    items = viewModel.woundPainType.value
                ){
                    viewModel.setWoundPainType(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                PopupTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.pain_intensity),
                    value = uiState.check.woundPainIntensite ?: "",
                    items = viewModel.woundPainIntensite.value
                ){
                    viewModel.setWoundPainIntensite(it.item)
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    placeholder = {
                        Text(text = stringResource(id = R.string.medication_type))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    value = uiState.check.medicationType ?: "",
                    onValueChange = {
                        viewModel.setMedicationType(it)
                    })

                Spacer(modifier = Modifier.height(16.dp))
                Divider(Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.check.images.isNotEmpty()) {
                    AddingImagesGallery(uiState.check.images) {
                        viewModel.removeImage(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                AddPhotoButton(onSuccess = {
                    viewModel.addImages(it)
                })
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    enabled = uiState.isValid && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.saveCheck()
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
                if (imeState.value){
                    Spacer(modifier = Modifier.height(150.dp))
                }
            }
        }
        if (viewModel.errorMessage != null){
            ErrorAlert(message = viewModel.errorMessage!!, onDismissRequest = {
                viewModel.errorMessage = null
            })
        }
        if (uiState.isSuccess){
            viewModel.clearData()
            onDismissRequest(true)
        }
    }
}

@Composable
@Preview
private fun CheckCreateDialogScreenPreview() {
    AppTheme {
        CheckCreateDialogScreen("", 0, 0, onDismissRequest = {})
    }
}