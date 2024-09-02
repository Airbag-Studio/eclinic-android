package it.airbagstudio.ticare.pages.drugsAdministration.scheduledDrugTherapies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.OperatingShift
import ch.ticare.eclinic.library.entity.ScheduledDrugTherapy
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.theme.AppTheme
import it.airbagstudio.ticare.ui.theme.specialTaskBackground
import it.airbagstudio.ticare.ui.theme.specialTaskColor
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT_ITA
import it.airbagstudio.ticare.utils.toDate
import it.airbagstudio.ticare.utils.toDayOfWeek
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import java.util.Calendar

data class ScheduledDrugTherapyItem(
    val from: String,
    val to: String,
    val drug: String,
    val administeringMode: String,
    val isSpecial: Boolean,
    val shiftsDrugAdministration: List<ScheduledDrugTherapyItem>
){
    data class ScheduledDrugTherapyItem(
        val shiftId: Int,
        val quantities: List<String?>
    )
}

@Composable
fun ScheduledDrugTherapyItemView(item: ScheduledDrugTherapyItem,shift: List<String>){
    val titleBgColor = if (item.isSpecial) specialTaskColor else MaterialTheme.colorScheme.surfaceVariant
    val contentBgColor = if (item.isSpecial) specialTaskBackground else MaterialTheme.colorScheme.surface

    Column {
        Text(
            modifier = Modifier.fillMaxWidth().background(
                titleBgColor).padding(horizontal = 16.dp, vertical = 8.dp),
            text = item.drug,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.background(contentBgColor).padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
            ) {
                TitleValueItem(modifier = Modifier.weight(1f).height(IntrinsicSize.Min), title = stringResource(R.string.start_date), value = item.from)
                VerticalDivider()
                TitleValueItem(modifier = Modifier.weight(1f).padding(start = 16.dp).height(IntrinsicSize.Min), title = stringResource(R.string.end_date), value = item.to)
            }
            HorizontalDivider()
            TitleValueItem(
                title = stringResource(R.string.administering_mode),
                value = item.administeringMode
            )
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Row {
                Spacer(Modifier.width(100.dp))
                LazyVerticalGrid(
                    modifier = Modifier.weight(1f),
                    columns = GridCells.Fixed(7)
                ) {
                    val daysOfWeek = listOf("L","M","M","G","V","S","D")
                    items(daysOfWeek) {
                        Text(
                            text = it,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                }
            }
            shift.forEachIndexed { index, s ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp)
                ){
                    Text(text = s,style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(100.dp))
                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f),
                        columns = GridCells.Fixed(7)
                    ) {
                        item.shiftsDrugAdministration[index].quantities.forEach {
                            item {
                                Text(
                                    modifier = Modifier.alpha(if (it == null) 0.5f else 1f),
                                    text = it ?: "-",
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }

    }

}



@Composable
private fun TitleValueItem(modifier: Modifier = Modifier,title: String, value: String){
    Column(modifier.then(Modifier.padding(vertical = 8.dp))) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview
private fun ScheduledDrugTherapyItemViewPreview(){
    val itemText = "{\"administeringMode\":\"orale\",\"drug\":\"LETROZOLE Sandoz cpr pell 2.5 mg\",\"drugNotes\":\"\",\"from\":\"16.05.2017\",\"isNonDaily\":false,\"isPatientOwnedDrug\":false,\"isSpecial\":true,\"isTaskAffecting\":true,\"lastExcLabel\":\"Ultima: 05.06.2023 17:28\",\"repetitionCode\":1,\"repetitionLabel\":\"Giornaliera | tutti i giorni\",\"shiftsDrugAdministration\":[{\"fullText\":\"11.08.2024 18:00 - 1 pce\",\"label\":\"Pomeriggio\",\"quantity\":\"1\",\"measureUnit\":\"pce\",\"shiftId\":2}],\"startUser\":\"Utente0194 Utente 194\",\"stopUser\":\"\",\"to\":\"\",\"typeActivePrinciplesCount\":1,\"typeGroupLabel\":\"CARE INDEX: Assortimento con ricetta\"}"
    val item = Json.decodeFromString<ScheduledDrugTherapy>(itemText)
    val shifts = listOf(
        "Mattina",
        "Mezzogiorno",
        "Pomeriggio",
        "Sera",
        "Notte"
    )
    val shiftsDrugAdministrations = mutableListOf<ScheduledDrugTherapyItem.ScheduledDrugTherapyItem>()
    shifts.forEachIndexed{ index, _ ->
        val therapiesForShift = item.shiftsDrugAdministration.filter { it.shiftId == index }
        val quantities = mutableListOf<String?>()
        for (i in 0..6) {
            val therapiesForDay = therapiesForShift.firstOrNull {
                val dateTimeString = it.fullText.split(" - ")
                val dateTime = dateTimeString[0].toDate(SERVER_PARAMETER_DATE_TIME_FORMAT_ITA)
                dateTime?.toDayOfWeek() == i
            }
            if (therapiesForDay != null) {
                quantities.add(therapiesForDay.quantity)
            }else{
                quantities.add(null)
            }
        }
        shiftsDrugAdministrations.add(ScheduledDrugTherapyItem.ScheduledDrugTherapyItem(
            shiftId = index,
            quantities = quantities
        ))
    }
    AppTheme {
        Scaffold {
            Column(modifier = Modifier.padding(it).fillMaxWidth()) {
                ScheduledDrugTherapyItemView(ScheduledDrugTherapyItem(
                    from = "16.05.2017",
                    to = "16.05.2017",
                    drug = "LETROZOLE Sandoz cpr pell 2.5 mg",
                    administeringMode = "orale",
                    shiftsDrugAdministration = shiftsDrugAdministrations,
                    isSpecial = item.isSpecial
                ),shift = shifts)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}