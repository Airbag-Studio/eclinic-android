package it.airbagstudio.ticare.pages.tasksGeneric.typeSelect

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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.TaskType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelectScreen(taskTypes: List<TaskType>, onSelectItem: (TaskType?) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp - 130
    ModalBottomSheet(
        onDismissRequest = {
            onSelectItem(null)
        },
        sheetState = sheetState,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.height(screenHeight.dp),
            content = {
                items(taskTypes) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                onSelectItem(it)
                            },
                        text = it.desc ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    HorizontalDivider()
                }


            })
    }
}