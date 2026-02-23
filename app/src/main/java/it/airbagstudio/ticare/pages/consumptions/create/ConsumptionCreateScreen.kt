package it.airbagstudio.ticare.pages.consumptions.create

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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.ticare.eclinic.library.entity.Article
import ch.ticare.eclinic.library.entity.EmployeeConsumption
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.utils.PlaceholderTransformation
import it.airbagstudio.ticare.utils.toDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumptionCreateScreen(
    viewModel: ConsumptionCreateViewModel = hiltViewModel(),
    article: Article?,
    consumption: EmployeeConsumption?,
    onDismissRequest: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (article != null) {
            viewModel.setSelectedArticleId(article.id)
        }
        if (consumption != null){
            viewModel.setSelectedArticleId(consumption.idItem)
            viewModel.setConsumptionId(consumption.id)
            viewModel.setNotes(consumption.remarks)
            viewModel.setDate(consumption.date.toDate("dd.MM.yyyy") ?: Date())
            viewModel.setQuantity(consumption.quantity.toString())
        }
    }
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            viewModel.clearData()
            onDismissRequest(false)
        }

    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = uiState.title)
                        }

                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.clearData()
                            onDismissRequest(false)
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            }) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                HorizontalDivider()
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row {
                        CalendarTextField(
                            enabled = uiState.item.editable,
                            modifier = Modifier.weight(1f),
                            date = uiState.item.date,
                            showTime = true,
                            label = {
                                Text(text = stringResource(id = R.string.actual_date))
                            },
                            onDateChanged = {
                                viewModel.setDate(it)
                            })
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            enabled = uiState.item.editable,
                            singleLine = true,
                            modifier = Modifier.width(100.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            visualTransformation = if (uiState.item.quantity.isEmpty()) PlaceholderTransformation(
                                "0"
                            ) else VisualTransformation.None,
                            label = {
                                Text(text = stringResource(id = R.string.quantity))
                            },
                            isError = uiState.item.quantity.toDoubleOrNull() == null,
                            value = uiState.item.quantity,
                            onValueChange = {
                                viewModel.setQuantity(it)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        enabled = uiState.item.editable,
                        label = {
                            Text(text = stringResource(id = R.string.notes))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        value = uiState.item.notes,
                        onValueChange = {
                            viewModel.setNotes(it)
                        }
                    )
                    if (uiState.item.editable) {
                        Spacer(modifier = Modifier.weight(0.4f))
                        Button(
                            enabled = !uiState.isLoading && uiState.isValid,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.saveConsumption()
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
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

            }
        }
        if (uiState.isSuccess) {
            viewModel.clearData()
            onDismissRequest(true)

        }
        if (uiState.errorMessage != null){
            ErrorAlert(message = uiState.errorMessage!!, onDismissRequest = {
                viewModel.clearErrors()
            })
        }
    }
}
