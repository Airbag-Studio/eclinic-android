package it.airbagstudio.ticare.pages.otherTreatments.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentItem
import it.airbagstudio.ticare.pages.otherTreatments.OtherTreatmentItemView
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentSearch(
    viewModel: TreatmentSearchViewModel = hiltViewModel(),
    state: SheetState,
    onDismissRequest: (Int?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest(null) },
        sheetState = state,
    ) {
        BuildContent(uiState.searchQuery,uiState.articles, onQueryChange = {
            viewModel.setSearchQuery(it)
        }, onItemSelected = {
            onDismissRequest(it)
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildContent(query: String, results: List<OtherTreatmentItem>, onQueryChange: (String) -> Unit,onItemSelected:(Int)->Unit) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier.height(screenHeight.dp)) {
        TextField(
            singleLine = true,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .height(72.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                focusRequester.freeFocus()
                focusManager.clearFocus(true)
            }),
            placeholder = {
                Text(text = stringResource(id = R.string.search))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            value = query,
            onValueChange = onQueryChange
        )
        LazyColumn(
            modifier = Modifier

                .wrapContentHeight()
                .background(Color.White),
            content = {
                items(results){
                    OtherTreatmentItemView(item = it,isSearch = true) {
                        onItemSelected(it.id)
                    }
                }
            })
        Spacer(modifier = Modifier.weight(1f))
    }


}


@Composable
@Preview
private fun SearchContentPreview() {
    AppTheme {
        BuildContent(query = "Test", results = listOf(
            OtherTreatmentItem("Medicamento Forfait", description = "Forfait per prestazioni terapeutiche Grado 01", number = "FPT01", hasDataToUpload = false)
        ), onQueryChange = {}, onItemSelected = {})
    }
}