package it.airbagstudio.ticare.pages.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.wounds.common.TitleValueView
import it.airbagstudio.ticare.ui.components.DrugChip
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.redColor
import it.airbagstudio.ticare.ui.theme.seed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryDrugAdministrationItemView(
    title: String,
    quantity: String,
    note: String,
    expectedQuantity: String,
    time: String,
    isConfirmed: Boolean,
    isReserve: Boolean,
    notExecuted: Boolean,
    rejected: Boolean
) {
    Column {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_diary_drug_administration),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

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
        if (note.isNotEmpty()) {
            NoteView(note = note)
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryNursingCourseItemView(
    title: String,
    duration: String,
    time: String,
    note: String,
    isPlanned: Boolean
) {
    Column {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_diary_nursing_course),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                BuildHeader(
                    category = stringResource(id = R.string.nursing_courses),
                    title = title
                )
                LabelValueRow(label = stringResource(id = R.string.duration), value = duration)
                if (!isPlanned) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        DrugChip(
                            label = stringResource(id = R.string.not_planned)
                        )
                    }
                }
            }
        }
        if (note.isNotEmpty()) {
            NoteView(note = note)
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiaryCarePlaneItemView(
    title: String,
    description: String,
    time: String,
    note: String,
    isPlanned: Boolean,
    duration: String
) {
    Column {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_diary_care_planes),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                BuildHeader(
                    category = stringResource(id = R.string.care_planes),
                    title = title
                )
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LabelValueRow(label = stringResource(id = R.string.duration), value = duration)
                if (!isPlanned) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        DrugChip(
                            label = stringResource(id = R.string.not_planned)
                        )
                    }
                }
            }
        }
        if (note.isNotEmpty()) {
            NoteView(note = note)
        }
    }

}

@Composable
fun DiaryVitaLParameterItemView(title: String, value: String, time: String, note: String) {
    Column {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_diary_vital_parameter),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                BuildHeader(
                    category = stringResource(id = R.string.vital_parameters),
                    title = title
                )
                LabelValueRow(label = stringResource(id = R.string.value), value = value)
            }
        }
        if (note.isNotEmpty()) {
            NoteView(note = note)
        }
    }

}

@Composable
fun DiaryWoundItemView(title: String, note: String) {
    Column {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_diary_wound),
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                BuildHeader(
                    category = stringResource(id = R.string.wounds_protocol),
                    title = title
                )
            }
        }
        if (note.isNotEmpty()) {
            NoteView(note = note)
        }
    }
}

@Composable
fun GenericDiaryListItemView(
    iconId: Int,
    typeIdLabel: Int,
    activityName: String,
    duration: String,
    time: String,
    note: String?,
    color: Color
) {
    Row(Modifier.padding(16.dp)) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .border(2.dp, color, RoundedCornerShape(66.dp))
                .padding(8.dp), painter = painterResource(id = iconId), contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            BuildHeader(title = activityName, category = stringResource(id = typeIdLabel))
            Row {
                LabelValueRow(label = stringResource(id = R.string.duration), value = duration)
                Spacer(modifier = Modifier.weight(1f))
                LabelValueRow(label = stringResource(id = R.string.time), value = time)
            }
            if (!note.isNullOrEmpty()) {
                NoteView(note = note)
            }

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
private fun NoteView(note: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(size = 5.dp)
            )
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        text = note,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium
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

@Preview
@Composable
private fun DiaryPreview() {
    AppTheme {
        Scaffold(Modifier.background(Color.White)) {
            Column(Modifier.padding(it)) {
                GenericDiaryListItemView(
                    iconId = R.drawable.ic_blood_exam_task,
                    typeIdLabel = R.string.blood_exam_task,
                    activityName = "Esami sangue",
                    duration = "12",
                    time = "8:30",
                    note = "Test note",
                    color = seed
                )

                DiaryDrugAdministrationItemView(
                    title = "Meto Zeroch cpr ret 25mg",
                    quantity = "2",
                    note = "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id es. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, to\n" +
                            "Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatu. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur ma.\n" +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
                    expectedQuantity = "2",
                    time = "09:30",
                    isConfirmed = true,
                    isReserve = true,
                    notExecuted = true,
                    rejected = true
                )

                DiaryNursingCourseItemView(
                    title = "Dolor sit amen",
                    duration = "23",
                    time = "14:56",
                    note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
                    isPlanned = false
                )
            }
        }
    }
}