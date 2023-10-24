package it.airbagstudio.ticare.pages.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import it.airbagstudio.ticare.ui.theme.AppTheme

@Composable
fun DiaryDrugAdministrationItemView() {
    Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_diary_drug_administration),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            BuildHeader(
                category = stringResource(id = R.string.drug_administration),
                title = "Meto Zeroch cpr ret 25mg"
            )
            Row {
                LabelValueRow(label = stringResource(id = R.string.quantity), value = "0")
                Spacer(modifier = Modifier.weight(1f))
                LabelValueRow(label = stringResource(id = R.string.prescribed), value = "0")
            }
            LabelValueRow(label = stringResource(id = R.string.time), value = "10:30")
        }
    }
}

@Composable
fun DiaryOtherServiceItemView() {
    Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_diary_other_services),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            BuildHeader(
                category = stringResource(id = R.string.other_prescriptions),
                title = "Medicamento Forfait",
                code = "FPT01"
            )
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = "Forfait per prestazioni terapeutiche Grado 01",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DiaryVitaLParameterItemView() {
    Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_diary_vital_parameter),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            BuildHeader(
                category = stringResource(id = R.string.vital_parameters),
                title = "Peso"
            )
            LabelValueRow(label = "Kg", value = "70")
            LabelValueRow(label = stringResource(id = R.string.time), value = "10:30")
        }
    }
}

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