package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.ticare.eclinic.library.entity.Zone
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun <T> MultiselectPopupTextField(
    modifier: Modifier = Modifier,
    label: String,
    items: List<ListPopupItem<T>>,
    selectedItems: List<ListPopupItem<T>>,
    enabled: Boolean = true,
    onClose: (List<ListPopupItem<T>>?) -> Unit
) {
    var showPopup by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = label)
            },
            value = if (selectedItems.isNotEmpty()) "     " else "",
            onValueChange = { },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.id_dropdown),
                    contentDescription = ""
                )
            },
            enabled = enabled
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp, end = 32.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = selectedItems.map { it.label }.joinToString(", ")
        )
        Box(modifier = Modifier
            .matchParentSize()
            .clickable {
                if(enabled) showPopup = true
            })
    }
    if (showPopup) {
        MultiSelectionListPopup(
            title = label,
            items = items,
            currentSelectedItems = selectedItems,
            onClose = {
                showPopup = false
                onClose(it)
            })
    }
}

@Composable
@Preview
private fun PreviewPopupTextField() {
    val items = listOf(
        ListPopupItem("Prova", Zone(id = 0, name = "Test")),
        ListPopupItem("Prova 2", Zone(id = 1, name = "Test 2")),
        ListPopupItem("Prova 3", Zone(id = 2, name = "Test 3")),
        ListPopupItem("Prova 4", Zone(id = 3, name = "Test 4")),
    )

    var selectedItems by remember {
        mutableStateOf<List<ListPopupItem<Zone>>>(listOf())
    }
    AppTheme {
        Column(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            MultiselectPopupTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Prova",
                items = items,
                selectedItems = selectedItems,
                onClose = {
                    selectedItems = it ?: listOf()
                })
        }
    }

}