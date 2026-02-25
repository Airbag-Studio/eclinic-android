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

private val DATE_COLUMN_WIDTH = 100.dp
private val PARAM_COLUMN_WIDTH = 120.dp
private val MIN_CELL_HEIGHT = 55.dp

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
                style = MaterialTheme.typography.bodyMedium,
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
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium,
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
 * Creates sample data for preview
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

    // Create sample dates
    val date1 = Date(System.currentTimeMillis())
    val date2 = Date(System.currentTimeMillis() - 86400000L) // 1 day ago
    val date3 = Date(System.currentTimeMillis() - 172800000L) // 2 days ago

    // Create rows
    val rows = listOf(
        // Row 1 - Most recent, with multiple measurements same day
        TableRow(
            date = "25.02.2026",
            dateObj = date1,
            cells = mapOf(
                "1" to TableCell(
                    listOf(
                        MeasurementEntry("08:30", "75.2", date1.time),
                        MeasurementEntry("18:45", "75.8", date1.time + 37200000)
                    )
                ),
                "blood_pressure" to TableCell(
                    listOf(
                        MeasurementEntry("08:30", "120/80", date1.time),
                        MeasurementEntry("18:45", "125/82", date1.time + 37200000)
                    )
                ),
                "3" to TableCell(
                    listOf(
                        MeasurementEntry("08:30", "98", date1.time)
                    )
                ),
                "4" to TableCell(emptyList()) // Empty cell
            )
        ),
        // Row 2 - Single measurements
        TableRow(
            date = "24.02.2026",
            dateObj = date2,
            cells = mapOf(
                "1" to TableCell(
                    listOf(
                        MeasurementEntry("11:15", "75.5", date2.time)
                    )
                ),
                "blood_pressure" to TableCell(
                    listOf(
                        MeasurementEntry("11:15", "118/78", date2.time)
                    )
                ),
                "3" to TableCell(
                    listOf(
                        MeasurementEntry("11:15", "97", date2.time)
                    )
                ),
                "4" to TableCell(
                    listOf(
                        MeasurementEntry("11:15", "36.5", date2.time)
                    )
                )
            )
        ),
        // Row 3 - Some missing values
        TableRow(
            date = "23.02.2026",
            dateObj = date3,
            cells = mapOf(
                "1" to TableCell(emptyList()),
                "blood_pressure" to TableCell(
                    listOf(
                        MeasurementEntry("10:00", "122/79", date3.time)
                    )
                ),
                "3" to TableCell(emptyList()),
                "4" to TableCell(
                    listOf(
                        MeasurementEntry("10:00", "36.8", date3.time)
                    )
                )
            )
        )
    )

    return VitalSignsTableData(columns = columns, rows = rows)
}
