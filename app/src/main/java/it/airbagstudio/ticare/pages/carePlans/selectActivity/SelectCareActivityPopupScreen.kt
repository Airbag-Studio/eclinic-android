package it.airbagstudio.ticare.pages.carePlans.selectActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCareActivityPopupScreen(
    caseCode: String,
    planId:Int,
    viewModel: SelectCareActivityPopupScreenViewModel = hiltViewModel(),
    onDismissRequest: (Pair<Boolean,Int>?) -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismissRequest(null) },
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) {
            viewModel.setCarePlanId(planId)
            viewModel.setPatientCode(caseCode)
        }
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(text = stringResource(id = R.string.new_care))
                        }

                    },
                    actions = {
                        IconButton(onClick = { onDismissRequest(null) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                )
            }) { values ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(values)

            ) {
                var tabIndex by remember {
                    mutableIntStateOf(0)
                }
                val labels = listOf(
                    stringResource(id = R.string.planned),
                    stringResource(id = R.string.not_planned)
                )
                TabRow(
                    selectedTabIndex = tabIndex,
                    indicator = { tabPositions ->
                        if (tabIndex < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[tabIndex]),
                            )
                        }
                    },
                ) {
                    labels.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = {
                                tabIndex = index
                            },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        )
                    }
                }
                when (tabIndex){
                    0 -> {
                        ItemsList(uiState.plannedActivities){
                            onDismissRequest(Pair(true,it))
                        }
                    }
                    1 -> {
                        SearchableList(uiState.query,uiState.unplannedActivities, onItemClick = {
                            onDismissRequest(Pair(false,it))
                        }){
                            viewModel.setQuery(it)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableList(query:String,activities: List<SelectCareActivityPopupUIState.ActivityListItem>,onItemClick:(Int)->Unit,onQueryChange: (String) -> Unit){
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Column {
        TextField(
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .height(56.dp),
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
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            value = query,
            onValueChange = {
                onQueryChange(it)
            }
        )
        ItemsList(activities,onItemClick)
    }
}

@Composable
private fun ItemsList(activities: List<SelectCareActivityPopupUIState.ActivityListItem>,onItemClick:(Int)->Unit){
    LazyColumn(
        content = {
            items(activities){
                ActivityListItemView(it.title){
                    onItemClick(it.id)
                }
            }
        })
}
