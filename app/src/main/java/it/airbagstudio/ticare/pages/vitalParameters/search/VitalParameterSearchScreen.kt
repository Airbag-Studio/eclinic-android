package it.airbagstudio.ticare.pages.vitalParameters.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.ticare.eclinic.library.entity.VitalSignType
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalParameterSearchScreen(
    state: SheetState,
    viewModel: VitalParameterSearchScreenViewModel = hiltViewModel(),
    onDismissRequest: (String?) -> Unit
){
    val items = viewModel.vitalSignTypes.collectAsState()
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest(null) },
        sheetState = state,
    ) {
        BuildContent(items.value,onDismissRequest)
    }
}

@Composable
private fun BuildContent(items: List<VitalSignType>,onDismissRequest: (String?) -> Unit){
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.height(screenHeight.dp),
        content = {
        items(items){
            Text(
                modifier = Modifier.fillMaxWidth().padding(16.dp).clickable {
                    onDismissRequest(it.code)
                },
                text = it.desc ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider()
        }
    })
}

@Composable
@Preview
private fun ContentPreview(){
    AppTheme {
        //BuildContent()
    }
}