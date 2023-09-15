package it.airbagstudio.ticare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class ListPopupItem(val label:String,val id:Int)

@Composable
fun ListPopup(title: String, items: Array<ListPopupItem>,setShowDialog: (Boolean) -> Unit, onItemSelected: (Int) -> Unit){
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(8.dp)
                    )
                    items.forEach { item ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.height(55.dp).clickable {
                                onItemSelected(item.id)
                            }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = item.label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        if (items.last() != item) {
                            Divider()
                        }

                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun ListPopupPreview(){
    ListPopup(title = "Zone", items = arrayOf(
        ListPopupItem("Prova", id = 1),
        ListPopupItem("Prova 2", id = 2)
    ), setShowDialog = {

    }, onItemSelected = {

    })
}