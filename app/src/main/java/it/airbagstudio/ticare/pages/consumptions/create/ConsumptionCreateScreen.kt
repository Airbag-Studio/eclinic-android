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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ch.ticare.eclinic.library.entity.Article
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.CalendarTextField
import it.airbagstudio.ticare.ui.theme.AppTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumptionCreateScreen(
    article: Article,
    onDismissRequest: (Boolean) -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismissRequest(false)
        }

    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = article.desc)
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
            ) {
                Divider()
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row {
                        CalendarTextField(
                            modifier = Modifier.weight(1f),
                            date = Date(),
                            label = {
                                Text(text = stringResource(id = R.string.actual_date_time))
                            },
                            onDateChanged = {

                            })
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            modifier = Modifier.width(100.dp),
                            label = {
                                Text(text = stringResource(id = R.string.quantity))
                            },
                            value = "",
                            onValueChange = {

                            }
                        )

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        label = {
                                Text(text = stringResource(id = R.string.notes))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        value = "", onValueChange = {}
                    )
                    Spacer(modifier = Modifier.weight(0.4f))
                    Button(
                        enabled = true,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {

                        }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.execute)
                        )
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
                    Spacer(modifier = Modifier.height(24.dp))
                }

            }
        }
    }
}

@Composable
@Preview
private fun PreviewConsumptionCreateScreen(){
    AppTheme {
        Scaffold {
            Column(Modifier.padding(it)) {

                ConsumptionCreateScreen(Article("","","",1)){

                }
            }
        }
    }

}