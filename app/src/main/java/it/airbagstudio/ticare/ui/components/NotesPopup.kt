package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPopup(startingText: String, editable: Boolean, onDone: (String?) -> Unit) {
    var text by remember {
        mutableStateOf(startingText)
    }
    val focusRequester = remember { FocusRequester() }
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDone(null)
        }) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                ToolbarWithBack(title = stringResource(id = R.string.notes), actions = {
                    if (editable) {
                        TextButton(onClick = {
                            onDone(text)
                        }) {
                            Text(text = stringResource(id = R.string.save))
                        }
                    }
                }) {
                    if (editable) {
                        focusRequester.freeFocus()
                    }
                    onDone(null)
                }
            }) { values ->
            Column(Modifier.padding(values)) {
                if (editable) {
                    OutlinedTextField(
                        placeholder = {
                            Text(text = stringResource(id = R.string.no_notes))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(16.dp)
                            .focusRequester(focusRequester),
                        value = text, onValueChange = {
                            text = it
                        })
                } else {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = text
                    )
                }

            }
        }
    }
    if (editable) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}