package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.CBISection
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SectionType

/**
 * Componente che mostra il riepilogo dei punteggi del form CBI.
 *
 * @param sections Lista delle sezioni del form
 * @param totalScore Punteggio totale del form
 * @param modifier Modifier per personalizzare il layout
 */
@Composable
fun ScoreSummarySection(
    sections: List<CBISection>,
    totalScore: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Riepilogo Punteggi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Punteggi parziali per ogni sezione
            sections.forEach { section ->
                ScoreRow(
                    title = getSectionTitle(section.type),
                    score = section.partialScore,
                    hasCorrectionFactor = section.correctionFactor > 1.0f
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Punteggio totale
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Punteggio Totale",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = totalScore.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Componente che mostra una riga del riepilogo dei punteggi.
 *
 * @param title Titolo della sezione
 * @param score Punteggio della sezione
 * @param hasCorrectionFactor Flag che indica se la sezione ha un fattore di correzione
 */
@Composable
private fun ScoreRow(
    title: String,
    score: Int,
    hasCorrectionFactor: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title + if (hasCorrectionFactor) " *" else "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Restituisce il titolo della sezione in base al tipo.
 *
 * @param sectionType Tipo di sezione
 * @return Titolo della sezione
 */
@Composable
private fun getSectionTitle(sectionType: SectionType): String {
    return when (sectionType) {
        SectionType.OBJECTIVE -> stringResource(R.string.section_objective)
        SectionType.PSYCHOLOGICAL -> stringResource(R.string.section_psychological)
        SectionType.PHYSICAL -> stringResource(R.string.section_physical)
        SectionType.SOCIAL -> stringResource(R.string.section_social)
        SectionType.EMOTIONAL -> stringResource(R.string.section_emotional)
    }
}
