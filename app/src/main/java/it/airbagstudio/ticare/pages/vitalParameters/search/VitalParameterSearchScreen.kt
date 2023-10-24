package it.airbagstudio.ticare.pages.vitalParameters.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalParameterSearchScreen(
    state: SheetState,
    onDismissRequest: (Int?) -> Unit
){
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest(null) },
        sheetState = state,
    ) {
        BuildContent()
    }
}

@Composable
private fun BuildContent(){
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.height(screenHeight.dp),
        content = {
        items(14){
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Row $it",
                style = MaterialTheme.typography.bodyLarge
            )
            Divider()
        }
    })
}

@Composable
@Preview
private fun ContentPreview(){
    AppTheme {
        BuildContent()
    }
}