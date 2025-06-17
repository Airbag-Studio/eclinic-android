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
 * Componente che mostra una scala di valutazione con radio button.
 *
 * @param selectedValue Valore selezionato
 * @param onValueSelected Callback per la selezione di un valore
 * @param minValue Valore minimo della scala (default: 0)
 * @param maxValue Valore massimo della scala (default: 4)
 * @param modifier Modifier per personalizzare il layout
 */
@Composable
fun RadioGroupScale(
    enabled : Boolean,
    selectedValue: Int?, // Changed to nullable
    onValueSelected: (Int) -> Unit,
    minValue: Int = 0,
    maxValue: Int = 4,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Radio button per la scala
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (value in minValue..maxValue) {
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
                        enabled = enabled,
                        selected = (value == selectedValue),
                        onClick = null // Gestito dal selectable
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (value < maxValue) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}
