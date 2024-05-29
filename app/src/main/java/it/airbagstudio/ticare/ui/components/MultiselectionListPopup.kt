package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.airbagstudio.ticare.R
import kotlin.reflect.KProperty

@Composable
fun <T> MultiSelectionListPopup(
    title: String,
    items: List<ListPopupItem<T>>,
    currentSelectedItems: List<ListPopupItem<T>>,
    onClose: (List<ListPopupItem<T>>?) -> Unit
) {
    var selectedItems by remember {
        mutableStateOf<List<ListPopupItem<T>>>(currentSelectedItems)
    }
    Dialog(onDismissRequest = { onClose(null) }) {
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items) { item ->
                        val selected = selectedItems.contains(item)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                            .height(45.dp)
                            .clickable {
                                if (selected) {
                                    selectedItems = selectedItems.minus(item)
                                } else {
                                    selectedItems = selectedItems.plus(item)
                                }
                            }) {
                            Icon(
                                modifier = Modifier.alpha(if (selected) 1f else 0f),
                                imageVector = Icons.Default.Check,
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = item.label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        if (items.last() != item) {
                            HorizontalDivider()
                        }
                    }

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(50.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        selectedItems = listOf()
                        onClose(null)
                    }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    TextButton(onClick = {
                        onClose(selectedItems)
                        selectedItems = listOf()
                    }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }

        }
    }
}

