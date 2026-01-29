package it.airbagstudio.ticare.pages.diary.allItemsTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.DiaryItem
import ch.ticare.eclinic.library.entity.ToolTag
import it.airbagstudio.ticare.pages.diary.DiaryCarePlaneItemView
import it.airbagstudio.ticare.pages.diary.DiaryDrugAdministrationItemView
import it.airbagstudio.ticare.pages.diary.DiaryNursingCourseItemView
import it.airbagstudio.ticare.pages.diary.DiaryVitaLParameterItemView
import it.airbagstudio.ticare.pages.diary.DiaryWoundItemView
import it.airbagstudio.ticare.pages.diary.GenericDiaryListItemView
import it.airbagstudio.ticare.ui.theme.seed
import it.airbagstudio.ticare.utils.getDiaryIconId
import it.airbagstudio.ticare.utils.getLabelId
import kotlin.collections.lastOrNull

@Composable
fun DiaryAllItemsTabContent(items: Map<String, List<DiaryItem>>){
    LazyColumn(content = {

        items(items.keys.toList()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                text = it
            )
            val items = items.get(it)
            items?.forEach { item ->
                if (item.entityName == "VitalSignTask") {
                    DiaryVitaLParameterItemView(
                        title = item.typeLbl ?: "",
                        value = item.value ?: "",
                        time = item.time,
                        note = item.taskNotes ?: "",
                        notExecuted = item.isSkipped ?: false,
                    )
                } else if (item.entityName == "PharmacologicalTask") {
                    DiaryDrugAdministrationItemView(
                        title = item.typeLbl ?: "",
                        quantity = item.actualQuantity ?: "",
                        note = item.taskNotes ?: "",
                        expectedQuantity = item.expQuantity ?: "",
                        time = item.time,
                        isConfirmed = true,
                        isReserve = item.isReserve ?: false,
                        notExecuted = item.isSkipped ?: false,
                        rejected = item.isRejected ?: false
                    )
                } else if (item.entityName == "HomeCareCourse"){
                    DiaryNursingCourseItemView(
                        title = item.title,
                        duration = if(item.duration != null) item.duration.toString() else "-",
                        time = item.time,
                        note = item.desc ?: "",
                        isPlanned = item.isScheduledTask ?: true
                    )
                } else if (item.entityName == "HomeCareServiceTask"){
                    DiaryCarePlaneItemView(
                        title = item.typeLbl ?: "",
                        // description = item.schedulerLbl ?: "",
                        isPlanned = item.isScheduledTask ?: true,
                        time = item.time,
                        note = item.taskNotes ?: "",
                        duration = item.duration?.toString() ?: "-"
                    )
                } else if (item.entityName == "Wound"){
                    DiaryWoundItemView(
                        title = item.bodyPart ?: "",
                        note = item.appearanceDescription ?: ""
                    )
                }else{
                    val toolTag = ToolTag.valueOf(item.entityName)
                    GenericDiaryListItemView(
                        iconId = toolTag.getDiaryIconId(),
                        typeIdLabel = toolTag.getLabelId(),
                        activityName = item.typeLbl ?: item.title,
                        duration = item.duration?.toString() ?: "-",
                        time = item.time,
                        note = if(toolTag.name.endsWith("Course")) item.desc else item.taskNotes,
                        color = if (item.taskNotes != null) seed else Color(0xFFCF4500),
                        notExecuted = item.isSkipped ?: false
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(start = if (items.lastOrNull() == item) 0.dp else 16.dp))
            }
        }
    })
}