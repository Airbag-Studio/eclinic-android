package it.airbagstudio.ticare.pages.patientDetails.form.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.QuestionResponse
import it.airbagstudio.ticare.pages.patientDetails.form.domain.model.SectionType
import it.airbagstudio.ticare.ui.theme.formColors

/**
 * Componente che mostra una sezione del form CBI con intestazione e domande.
 *
 * @param sectionType Tipo di sezione
 * @param questions Lista delle domande della sezione
 * @param onScoreChanged Callback per il cambio di punteggio di una domanda
 * @param partialScore Punteggio parziale della sezione
 * @param modifier Modifier per personalizzare il layout
 * @param isInvalid Flag per indicare se la sezione contiene campi invalidi
 */
@Composable
fun FormSection(
    enabled : Boolean,
    sectionType: SectionType,
    questions: List<QuestionResponse>,
    onScoreChanged: (Int, Int) -> Unit,
    partialScore: Int,
    modifier: Modifier = Modifier,
    isInvalid: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.formColors.sectionBackground
        ),
        border = if (isInvalid) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Intestazione della sezione
            SectionHeader(
                title = getSectionTitle(sectionType),
                score = partialScore
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Domande della sezione
            questions.forEach { question ->
                QuestionItem(
                    enabled = enabled,
                    questionNumber = question.questionId,
                    questionText = question.questionText,
                    selectedScore = question.score,
                    onScoreSelected = { score ->
                        onScoreChanged(question.questionId, score)
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
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
