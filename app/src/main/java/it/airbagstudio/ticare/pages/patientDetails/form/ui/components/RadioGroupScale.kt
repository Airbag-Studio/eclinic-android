package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Componente che mostra una scala di valutazione da 0 a 4 con radio button.
 *
 * @param selectedValue Valore selezionato (0-4)
 * @param onValueSelected Callback per la selezione di un valore
 * @param modifier Modifier per personalizzare il layout
 */
@Composable
fun RadioGroupScale(
    selectedValue: Int?, // Changed to nullable
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Etichette della scala
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Per nulla",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Molto",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Radio button per la scala 0-4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (value in 0..4) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .selectable(
                            selected = (value == selectedValue),
                            onClick = { onValueSelected(value) },
                            role = Role.RadioButton
                        )
                        .padding(4.dp)
                ) {
                    RadioButton(
                        selected = (value == selectedValue),
                        onClick = null // Gestito dal selectable
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (value < 4) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}
