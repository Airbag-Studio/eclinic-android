package it.airbagstudio.ticare.pages.vitalParameters.create

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.theme.AppTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewVitalParameterSheet(
    sheetState: SheetState,
    onDismissRequest: (Boolean) -> Unit
) {




    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismissRequest(false)
        }) {
        BuildSheetContent()
    }
}
@Composable
private fun BuildSheetContent(){
    //temp values
    var date by remember {
        mutableStateOf(Date())
    }

    var notes by remember{
        mutableStateOf("")
    }
    Column(modifier = Modifier
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        Text(
            text = "Pressione arteriosa",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = {
                    Text("mm/hg")
                },
                value = "",
                onValueChange = {}
            )
            Spacer(modifier = Modifier.width(24.dp))
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = {
                    Text(stringResource(id = R.string.duration))
                },
                value = "",
                onValueChange = {}
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        CalendarTextField(
            modifier = Modifier.fillMaxWidth(),
            date = date,
            label = { Text(text = stringResource(id = R.string.actual_date_time)) },
            onDateChanged = {
                date = it
            })
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = {
                Text(text = stringResource(id = R.string.notes))
            },
            value = notes,
            onValueChange = {
                notes = it
            })
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.show_in_diary))
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "")
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.save))
            if (false) {
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun VitalParamPreview(){
    AppTheme {
        BuildSheetContent()
    }
}