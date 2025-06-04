package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Componente che mostra una singola domanda con la scala di valutazione.
 *
 * @param questionNumber Numero della domanda
 * @param questionText Testo della domanda
 * @param selectedScore Punteggio selezionato (0-4)
 * @param onScoreSelected Callback per la selezione di un punteggio
 * @param modifier Modifier per personalizzare il layout
 */
@Composable
fun QuestionItem(
    questionNumber: Int,
    questionText: String,
    selectedScore: Int?, // Changed to nullable
    onScoreSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface // tonalElevation removed to avoid primary color tint
        // tonalElevation = 1.dp 
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Numero e testo della domanda
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$questionNumber.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Text(
                    text = questionText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scala di valutazione
            RadioGroupScale(
                selectedValue = selectedScore,
                onValueSelected = onScoreSelected
            )
        }
    }
}
