package it.airbagstudio.ticare.pages.drugsAdministration.editDrugAdministration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.ToolbarWithBack
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    startingText: String,
    onBack: (String) -> Unit
) {
    var text by remember {
        mutableStateOf(startingText)
    }
    val focusRequester = remember { FocusRequester() }
    Scaffold(
        topBar = {
            ToolbarWithBack(title = stringResource(id = R.string.notes), actions = {
                TextButton(onClick = {
                    focusRequester.freeFocus()
                    onBack(text)
                }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }) {
                focusRequester.freeFocus()
                onBack(startingText)
            }
        },

        ) { values ->
        Column(modifier = Modifier.padding(values)) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                value = text, onValueChange = {
                    text = it
                })
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
@Preview
fun EditNoteScreenPreview() {
    AppTheme {
        EditNoteScreen(startingText = "", onBack = {})
    }
}