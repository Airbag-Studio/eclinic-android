package it.airbagstudio.ticare.pages.patientsList

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.navigation.NavigationActions
import it.airbagstudio.ticare.ui.components.DropDownButton
import it.airbagstudio.ticare.ui.components.PatientImage
import it.airbagstudio.ticare.ui.components.PatientListItem
import it.airbagstudio.ticare.ui.components.PatientListItemView
import it.airbagstudio.ticare.ui.components.PatientListItemViewLoading
import it.airbagstudio.ticare.ui.components.ToolbarWithSyncAndSettings
import it.airbagstudio.ticare.ui.components.shimmerBrush
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    viewModel: PatientListScreenViewModel = hiltViewModel(),
    navActions: NavigationActions

) {
    var searchActive by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ToolbarWithSyncAndSettings(title = "Casa Delle Rose") {

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
            Box(modifier = Modifier
                //.padding(horizontal = 16.dp)

                .fillMaxWidth()

            ) {
                SearchBar(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .fillMaxWidth(),
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
                        if (searchActive){
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
                    /*
                    repeat(4) { idx ->
                        val resultText = "Suggestion $idx"
                        ListItem(
                            headlineContent = { Text(resultText) },
                            supportingContent = { Text("Additional info") },
                            leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                            modifier = Modifier

                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                     */
                }
                Column(modifier = Modifier.padding(top = 70.dp)) {
                    Row(modifier = Modifier
                        .padding(8.dp)) {
                        DropDownButton(modifier = Modifier.weight(1f), value = stringResource(id = R.string.zones), isEnabled = !viewModel.isLoading) {

                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        DropDownButton(modifier = Modifier.weight(1f),value = stringResource(id = R.string.micro_zones), isEnabled = !viewModel.isLoading) {

                        }
                    }
                    if(viewModel.isLoading){
                        repeat(8) {
                            PatientListItemViewLoading()
                        }
                    }else {
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(viewModel.patients) { patientListItem ->
                                PatientListItemView(patient = patientListItem) {
                                    navActions.navigateToPatientDetails(Uri.encode(patientListItem.cOD))
                                }
                            }
                        }
                    }
                }

            }


            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
@Preview
private fun PagePreview() {
    val vm = PatientListScreenViewModel()
    vm.patients = listOf(
        PatientListItem(
            surname = "ABETE",
            name = "Maria",
            address = "Via la Montagna 16",
            cAP = "6962",
            cOD = "23/2172",
            locality = "Viganello",
            birthday = "03.08.1936",
            age = 87,
            photo = null
            //photo = "https://www.tag24.it/wp-content/uploads/2023/04/WhatsApp-Image-2023-03-31-at-14.12.21-e1680618678712-800x560.jpeg"
        ),
        PatientListItem(
            surname = "ABETE",
            name = "Maria",
            address = "Via la Montagna 16",
            cAP = "6962",
            cOD = "23/2172",
            locality = "Viganello",
            birthday = "03.08.1936",
            age = 87,
            //photo = null
             photo = "https://www.tag24.it/wp-content/uploads/2023/04/WhatsApp-Image-2023-03-31-at-14.12.21-e1680618678712-800x560.jpeg"
        )
    )
    AppTheme() {
        PatientListScreen(viewModel = vm,navActions = NavigationActions(NavController(LocalContext.current)))
    }

}