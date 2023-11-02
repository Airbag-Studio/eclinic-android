package it.airbagstudio.ticare.pages.carePlans.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ticare.eclinic.library.entity.HomeCareActivity
import coil.compose.AsyncImagePainter.State.Empty.painter
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.ui.components.DrugChip
import it.airbagstudio.ticare.ui.theme.AppTheme

data class CarePlanCoursesListItem(
    val title: String,
    val executed: Boolean,
    val id: Int,
    val planned: Boolean,
    val activity: HomeCareActivity
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CarePlanCoursesListItemView(item: CarePlanCoursesListItem,onClick: (HomeCareActivity) -> Unit){
    Row(
        modifier = Modifier.clickable {
            onClick(item.activity)
        }.padding(16.dp,8.dp,24.dp,8.dp)
    ) {
        if (item.executed){
            Image(painter = painterResource(id = R.drawable.ic_check), contentDescription = "")
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!item.planned){
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    DrugChip(label = stringResource(id = R.string.not_planned))
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = ""
        )

    }
}
/*
@Composable
@Preview
private fun CarePlanCoursesListItemViewPreview(){
    AppTheme {
        Column(Modifier.background(Color.White)) {
            CarePlanCoursesListItemView(CarePlanCoursesListItem("Utilizzo MLL - Limitazioni meccaniche",true,1,false)){

            }
            Divider()
            CarePlanCoursesListItemView(CarePlanCoursesListItem("Utilizzo MLL - Limitazioni meccaniche",false,1,true)){

            }
        }
    }
}

 */