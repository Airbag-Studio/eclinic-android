package it.airbagstudio.ticare.pages.drugsAdministration.scheduledDrugTherapies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledDrugTherapyView(
    viewModel: ScheduledDrugTherapyViewModel = hiltViewModel(),
    patientCode: String,
    onDismiss: () -> Unit
){
    LaunchedEffect(Unit) {
        viewModel.getScheduledDrugTherapies(patientCode)
    }
    val therapies by viewModel.scheduledDrugTherapies.collectAsStateWithLifecycle()
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.therapies))
                        }

                    },
                    actions = {
                        IconButton(onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = ""
                            )
                        }
                    }
                )
            }
        ) {
            Column(Modifier.padding(it)) {
                LazyColumn {
                    items(therapies){ therapy ->
                        ScheduledDrugTherapyItemView(therapy,viewModel.shifts)
                    }
                }
            }
        }
    }
}