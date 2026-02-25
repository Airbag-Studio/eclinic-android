package it.airbagstudio.ticare.pages.vitalParameters.charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.MeasurementEntry
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.TableCell
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.TableColumn
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.TableRow
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.VitalSignsTableData
import java.util.Date
import java.util.Locale

private val DATE_COLUMN_WIDTH = 100.dp
private val PARAM_COLUMN_WIDTH = 120.dp
private val MIN_CELL_HEIGHT = 60.dp

/**
 * Main table component for displaying vital signs data
 */
@Composable
fun VitalSignsTable(
    tableData: VitalSignsTableData,
    modifier: Modifier = Modifier
) {
    if (tableData.isEmpty()) {
        EmptyTableState(modifier = modifier)
        return
    }

    // Shared scroll state for all rows
    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        // Header row
        TableHeaderRow(columns = tableData.columns, scrollState = scrollState)

        // Data rows
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(tableData.rows) { row ->
                TableDataRow(row = row, columns = tableData.columns, scrollState = scrollState)
            }
        }
    }
}

/**
 * Header row with column titles
 */
@Composable
private fun TableHeaderRow(
    columns: List<TableColumn>,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Row(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Date column header (fixed)
        TableHeaderCell(
            text = "Data",
            width = DATE_COLUMN_WIDTH
        )

        // Parameter columns (scrollable)
        Row(
            modifier = Modifier.horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            columns.forEach { column ->
                val headerText = if (column.unit.isNotEmpty()) {
                    "${column.title} (${column.unit})"
                } else {
                    column.title
                }
                TableHeaderCell(
                    text = headerText,
                    width = PARAM_COLUMN_WIDTH
                )
            }
        }
    }
}

/**
 * Single data row
 */
@Composable
private fun TableDataRow(
    row: TableRow,
    columns: List<TableColumn>,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Max).border(0.5.dp, MaterialTheme.colorScheme.outline),
        horizontalArrangement = Arrangement.spacedBy(0.dp),


    ) {
        // Date column (fixed, different background)
        Box(
            modifier = Modifier
                .width(DATE_COLUMN_WIDTH)
                .fillMaxHeight()

                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = row.date,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        // Parameter columns (scrollable)
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            columns.forEach { column ->
                val cell = row.cells[column.vitalSignId]
                TableDataCell(
                    cell = cell,
                    width = PARAM_COLUMN_WIDTH
                )
            }
        }
    }
}

/**
 * Header cell component
 */
@Composable
private fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(MIN_CELL_HEIGHT)
            .border(0.5.dp, MaterialTheme.colorScheme.outline),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Data cell component
 */
@Composable
private fun TableDataCell(cell: TableCell?, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .border(0.5.dp, MaterialTheme.colorScheme.outline),
        contentAlignment = Alignment.Center
    ) {
        if (cell == null || cell.isEmpty()) {
            // Empty cell
            Text(
                text = "-",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        } else {
            // Cell with measurements
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                cell.measurements.forEach { entry ->
                    Text(
                        text = "${entry.time} - ${entry.value}",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Empty state when no data is available
 */
@Composable
private fun EmptyTableState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nessun dato disponibile",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// Preview
@Preview(showBackground = true, heightDp = 600)
@Composable
private fun VitalSignsTablePreview() {
    MaterialTheme {
        VitalSignsTable(
            tableData = createSampleTableData(),
            modifier = Modifier.fillMaxSize()
        )
    }
}
/**
 * Creates sample data for preview with many rows to test scrolling
 */
private fun createSampleTableData(): VitalSignsTableData {
    // Define columns
    val columns = listOf(
        TableColumn(
            vitalSignId = "1",
            title = "Peso",
            unit = "kg",
            isDualValue = false
        ),
        TableColumn(
            vitalSignId = "blood_pressure",
            title = "Pressione Arteriosa",
            unit = "mmHg",
            isDualValue = true
        ),
        TableColumn(
            vitalSignId = "3",
            title = "Saturazione",
            unit = "%",
            isDualValue = false
        ),
        TableColumn(
            vitalSignId = "4",
            title = "Temperatura",
            unit = "°C",
            isDualValue = false
        )
    )

    // Generate many rows to test vertical scrolling (25 days of data)
    val rows = mutableListOf<TableRow>()
    val baseTime = System.currentTimeMillis()

    for (dayOffset in 0 until 25) {
        val date = Date(baseTime - (dayOffset * 86400000L))
        val day = 25 - dayOffset
        val month = 2
        val dateString = String.format(Locale.getDefault(), "%02d.%02d.2026", day, month)

        // Vary data to make it more realistic
        val hasMultipleMeasurements = dayOffset % 3 == 0
        val hasMissingValues = dayOffset % 5 == 0

        val weight = 74.5f + (dayOffset * 0.1f)
        val systolic = 118 + (dayOffset % 10)
        val diastolic = 76 + (dayOffset % 6)
        val saturation = 96 + (dayOffset % 4)
        val temperature = 36.4f + ((dayOffset % 8) * 0.1f)

        rows.add(
            TableRow(
                date = dateString,
                dateObj = date,
                cells = mapOf(
                    "1" to TableCell(
                        if (hasMultipleMeasurements) listOf(
                            MeasurementEntry("08:30", "%.1f".format(weight), date.time),
                            MeasurementEntry("18:00", "%.1f".format(weight + 0.3f), date.time + 34200000)
                        ) else if (!hasMissingValues) listOf(
                            MeasurementEntry("10:00", "%.1f".format(weight), date.time)
                        ) else emptyList()
                    ),
                    "blood_pressure" to TableCell(
                        if (hasMultipleMeasurements) listOf(
                            MeasurementEntry("08:30", "$systolic/$diastolic", date.time),
                            MeasurementEntry("18:00", "${systolic + 5}/${diastolic + 2}", date.time + 34200000)
                        ) else listOf(
                            MeasurementEntry("10:00", "$systolic/$diastolic", date.time)
                        )
                    ),
                    "3" to TableCell(
                        if (!hasMissingValues) listOf(
                            MeasurementEntry("10:00", "$saturation", date.time)
                        ) else emptyList()
                    ),
                    "4" to TableCell(
                        if (hasMultipleMeasurements) listOf(
                            MeasurementEntry("08:30", "%.1f".format(temperature), date.time),
                            MeasurementEntry("18:00", "%.1f".format(temperature + 0.2f), date.time + 34200000)
                        ) else if (dayOffset % 4 != 0) listOf(
                            MeasurementEntry("10:00", "%.1f".format(temperature), date.time)
                        ) else emptyList()
                    )
                )
            )
        )
    }

    return VitalSignsTableData(columns = columns, rows = rows)
}
