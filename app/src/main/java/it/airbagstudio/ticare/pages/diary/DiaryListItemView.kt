package it.airbagstudio.ticare.pages.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.DrugChip
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.redColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryDrugAdministrationItemView(
    title: String,
    quantity: String,
    expectedQuantity: String,
    time: String,
    isConfirmed: Boolean,
    isReserve: Boolean,
    notExecuted: Boolean,
    rejected: Boolean
) {
    Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_diary_drug_administration),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            BuildHeader(
                category = stringResource(id = R.string.drug_administration),
                title = title
            )
            Row {
                LabelValueRow(label = stringResource(id = R.string.quantity), value = quantity)
                if (!isReserve) {
                    Spacer(modifier = Modifier.weight(1f))
                    LabelValueRow(
                        label = stringResource(id = R.string.prescribed),
                        value = expectedQuantity
                    )
                }
            }
            LabelValueRow(label = stringResource(id = R.string.time), value = time)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (!isConfirmed) {
                    DrugChip(
                        label = stringResource(id = R.string.not_confirmed),
                        textColor = redColor
                    )
                }
                if (notExecuted) {
                    DrugChip(label = stringResource(id = R.string.not_performed))
                }
                if (isReserve) {
                    DrugChip(label = stringResource(id = R.string.reserves))
                }
                if (rejected) {
                    DrugChip(label = stringResource(id = R.string.rejected_by_patient))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryNursingCourseItemView(title: String,description:String,isPlanned:Boolean) {
    Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_diary_nursing_course),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            BuildHeader(
                category = stringResource(id = R.string.nursing_courses),
                title = title
            )
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isPlanned){
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    DrugChip(
                        label = stringResource(id = R.string.not_planned),
                        textColor = redColor
                    )
                }
            }
        }
    }
}

@Composable
fun DiaryVitaLParameterItemView(title: String, value: String, time: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_diary_vital_parameter),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            BuildHeader(
                category = stringResource(id = R.string.vital_parameters),
                title = title
            )
            LabelValueRow(label = stringResource(id = R.string.value), value = value)
            LabelValueRow(label = stringResource(id = R.string.time), value = time)
        }
    }
}
/*
@Composable
@Preview
private fun PreviewDiaryItemView() {
    AppTheme {
        Column {
            DiaryDrugAdministrationItemView()
            Divider()
            DiaryOtherServiceItemView()
            Divider()
            DiaryVitaLParameterItemView()
        }

    }
}

 */


@Composable
private fun BuildHeader(category: String, title: String, code: String = "") {
    Column {
        Text(
            text = category,
            style = MaterialTheme.typography.labelMedium
        )
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.bodyLarge

            )
            Text(
                text = code,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


@Composable
private fun DrugChip(label: String, textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .border(
                width = 0.5.dp,
                color = Color(0xFF41484D),
                shape = RoundedCornerShape(size = 99.dp)
            )
            .padding(vertical = 2.dp, horizontal = 8.dp),
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}

@Composable
private fun LabelValueRow(label: String, value: String) {
    Row() {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}