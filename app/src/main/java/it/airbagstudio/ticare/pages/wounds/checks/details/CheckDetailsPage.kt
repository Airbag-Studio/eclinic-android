package it.airbagstudio.ticare.pages.wounds.checks.details

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import it.airbagstudio.ticare.R
import it.airbagstudio.ticare.pages.wounds.common.ImagesDialog
import it.airbagstudio.ticare.pages.wounds.common.TitleValueView
import it.airbagstudio.ticare.ui.components.ErrorAlert
import it.airbagstudio.ticare.ui.components.ToolbarWithBackAndSync
import it.airbagstudio.ticare.ui.components.okHttpClient
import it.airbagstudio.ticare.utils.format
import it.airbagstudio.ticare.utils.getPainter
import it.airbagstudio.ticare.utils.toDate
import java.time.Instant
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckDetailsPage(
    viewModel: CheckDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var showImagesDialog by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            ToolbarWithBackAndSync(title = stringResource(id = R.string.check)) {
                onBack()
            }
        }
    ) { paddingValues ->
        if (viewModel.errorMessage != null) {
            ErrorAlert(
                message = viewModel.errorMessage!!,
                onDismissRequest = { viewModel.errorMessage = null })
        }
        viewModel.check?.let { check ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                TitleValueView(title = stringResource(id = R.string.date_and_time), value =  check.dateTime.toDate("dd.MM.yyyy HH:mm")?.format("dd MMMM yyyy, HH:mm") ?: "")
                Divider()
                TitleValueView(title = stringResource(id = R.string.surface), value =  check.area)
                Divider()
                TitleValueView(title = stringResource(id = R.string.depth), value =  check.depth)
                Divider()
                TitleValueView(title = stringResource(id = R.string.necrosis_zones), value =  check.necrosis)
                Divider()
                TitleValueView(title = stringResource(id = R.string.fibrin), value =  check.fibrin)
                Divider()
                TitleValueView(title = stringResource(id = R.string.granulation_tissue), value =  check.granulationTissue)
                Divider()
                TitleValueView(title = stringResource(id = R.string.smell), value =  check.smell)
                Divider()
                TitleValueView(title = stringResource(id = R.string.secretion), value =  check.secretion)
                Divider()
                TitleValueView(title = stringResource(id = R.string.surrounding_skin), value =  check.surroundingSkin)
                Divider()
                TitleValueView(title = stringResource(id = R.string.pain), value =  check.pain)
                Divider()
                TitleValueView(title = stringResource(id = R.string.pain_intensity), value =  check.painIntensity)
                Divider()
                TitleValueView(title = stringResource(id = R.string.medication_type), value =  check.medicationType)
                Divider()
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            showImagesDialog = true
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_photos),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "${viewModel.photos.count()}")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = ""
                    )
                }
                Divider()
            }
        } ?: run {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }
        if (showImagesDialog) {
            val woundDate = viewModel.check?.dateTime?.toDate("dd.MM.yyyy HH:mm")?.format("dd MMMM yyyy, HH:mm") ?: ""
            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .okHttpClient(okHttpClient)
                .build()

            val painters = viewModel.photos?.filter { it.iDCheck == viewModel.checkId.toInt() }?.map {
                it.getPainter(requestData = viewModel.requestImageRequestData, imageLoader = imageLoader, authTimestampHeader = DateTimeFormatter.ISO_INSTANT.format(
                    Instant.now()), isOnline = viewModel.isOnline)
            } ?: listOf()

            ImagesDialog(
                date = woundDate,
                painters = painters
            ) {
                showImagesDialog = false
            }
        }
    }
}