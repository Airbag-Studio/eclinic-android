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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.Zone
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun <T> PopupTextField(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    enabled: Boolean = true,
    label: String,
    value: String,
    items: List<ListPopupItem<T>>,
    onItemSelected: (ListPopupItem<T>) -> Unit
) {
    var showPopup by remember { mutableStateOf(false) }

    val labelComposable: @Composable() (() -> Unit)? =
        if (showLabel) {
            {
                Text(text = label)
            }
        } else {
            null
        }


    Box(modifier = modifier) {
        OutlinedTextField(
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            label = labelComposable,
            value = value,
            onValueChange = { },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.id_dropdown),
                    contentDescription = ""
                )
            }
        )
        Box(modifier = Modifier
            .matchParentSize()
            .clickable {
                showPopup = true
            })
    }
    if (showPopup) {
        ListPopup(title = label, items = items, setShowDialog = {
            showPopup = it
        }, onItemSelected = {
            onItemSelected(it)
            showPopup = false
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
    var value by remember { mutableStateOf("") }
    AppTheme {
        Column(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            PopupTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Prova",
                value = value,
                items = items,
                onItemSelected = {
                    value = it.item?.name ?: ""
                })
        }
    }

}