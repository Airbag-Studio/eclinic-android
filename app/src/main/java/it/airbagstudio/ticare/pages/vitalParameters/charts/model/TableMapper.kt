package it.airbagstudio.ticare.pages.vitalParameters.charts.model

import it.airbagstudio.ticare.utils.DATE_ONLY_TIME_FORMAT
import it.airbagstudio.ticare.utils.SERVER_DATE_FORMAT
import it.airbagstudio.ticare.utils.format
import java.util.Date

/**
 * Transforms chart configurations into table data structure
 */
object TableMapper {

    /**
     * Main transformation function: converts chart configs to table data
     */
    fun mapToTableData(chartConfigs: List<VitalSignChartConfig>): VitalSignsTableData {
        if (chartConfigs.isEmpty()) {
            return VitalSignsTableData(emptyList(), emptyList())
        }

        // Step 1: Build columns (only for parameters with data)
        val columns = buildColumns(chartConfigs)

        if (columns.isEmpty()) {
            return VitalSignsTableData(emptyList(), emptyList())
        }

        // Step 2: Extract all unique dates and build rows
        val rows = buildRows(chartConfigs, columns)

        return VitalSignsTableData(
            columns = columns,
            rows = rows.sortedByDescending { it.dateObj } // Most recent first
        )
    }

    /**
     * Builds column definitions from chart configs
     */
    private fun buildColumns(chartConfigs: List<VitalSignChartConfig>): List<TableColumn> {
        val columns = mutableListOf<TableColumn>()

        chartConfigs.forEach { config ->
            // Check if this is a dual-line chart (blood pressure)
            if (config.chartType is ChartType.DualLine && config.series.size == 2) {
                // Blood pressure: single column for systolic/diastolic
                if (config.series.any { it.dataPoints.isNotEmpty() }) {
                    columns.add(
                        TableColumn(
                            vitalSignId = "blood_pressure",
                            title = config.title,
                            unit = config.yAxisLabel ?: "",
                            isDualValue = true
                        )
                    )
                }
            } else {
                // Single-line charts
                config.series.forEach { series ->
                    if (series.dataPoints.isNotEmpty()) {
                        columns.add(
                            TableColumn(
                                vitalSignId = series.seriesId,
                                title = config.title,
                                unit = config.yAxisLabel ?: "",
                                isDualValue = false
                            )
                        )
                    }
                }
            }
        }

        return columns
    }

    /**
     * Builds table rows from chart configs, grouping data by date
     */
    private fun buildRows(
        chartConfigs: List<VitalSignChartConfig>,
        columns: List<TableColumn>
    ): List<TableRow> {
        // Map to store all measurements grouped by date string
        val dateToMeasurementsMap = mutableMapOf<String, MutableMap<String, MutableList<MeasurementEntry>>>()
        val dateToDateObjMap = mutableMapOf<String, Date>()

        chartConfigs.forEach { config ->
            if (config.chartType is ChartType.DualLine && config.series.size == 2) {
                // Handle blood pressure (dual values)
                processBloodPressure(config, dateToMeasurementsMap, dateToDateObjMap)
            } else {
                // Handle single-line charts
                config.series.forEach { series ->
                    processSingleSeries(series, dateToMeasurementsMap, dateToDateObjMap)
                }
            }
        }

        // Convert map to list of TableRow
        return dateToMeasurementsMap.map { (dateStr, measurementsMap) ->
            val cells = columns.associate { column ->
                val measurements = measurementsMap[column.vitalSignId] ?: emptyList()
                column.vitalSignId to TableCell(measurements.sortedBy { it.timestamp })
            }

            TableRow(
                date = dateStr,
                dateObj = dateToDateObjMap[dateStr] ?: Date(),
                cells = cells
            )
        }
    }

    /**
     * Processes blood pressure data (systolic/diastolic pairing)
     */
    private fun processBloodPressure(
        config: VitalSignChartConfig,
        dateToMeasurementsMap: MutableMap<String, MutableMap<String, MutableList<MeasurementEntry>>>,
        dateToDateObjMap: MutableMap<String, Date>
    ) {
        val systolicSeries = config.series[0]
        val diastolicSeries = config.series[1]

        // Create a map of timestamp to systolic values
        val systolicMap = systolicSeries.dataPoints.associateBy { it.timestamp }
        val diastolicMap = diastolicSeries.dataPoints.associateBy { it.timestamp }

        // Get all unique timestamps
        val allTimestamps = (systolicMap.keys + diastolicMap.keys).toSet()

        allTimestamps.forEach { timestamp ->
            val systolicPoint = systolicMap[timestamp]
            val diastolicPoint = diastolicMap[timestamp]

            // Only create entry if both values exist
            if (systolicPoint != null && diastolicPoint != null) {
                val dateStr = systolicPoint.dateTime.format(SERVER_DATE_FORMAT)
                val timeStr = systolicPoint.dateTime.format(DATE_ONLY_TIME_FORMAT)

                // Store date object for sorting
                dateToDateObjMap.putIfAbsent(dateStr, systolicPoint.dateTime)

                // Format as "systolic/diastolic"
                val value = "${systolicPoint.value.toInt()}/${diastolicPoint.value.toInt()}"

                val entry = MeasurementEntry(
                    time = timeStr,
                    value = value,
                    timestamp = timestamp
                )

                dateToMeasurementsMap
                    .getOrPut(dateStr) { mutableMapOf() }
                    .getOrPut("blood_pressure") { mutableListOf() }
                    .add(entry)
            }
        }
    }

    /**
     * Processes a single series (non-blood pressure)
     */
    private fun processSingleSeries(
        series: ChartLineSeries,
        dateToMeasurementsMap: MutableMap<String, MutableMap<String, MutableList<MeasurementEntry>>>,
        dateToDateObjMap: MutableMap<String, Date>
    ) {
        series.dataPoints.forEach { dataPoint ->
            val dateStr = dataPoint.dateTime.format(SERVER_DATE_FORMAT)
            val timeStr = dataPoint.dateTime.format(DATE_ONLY_TIME_FORMAT)

            // Store date object for sorting
            dateToDateObjMap.putIfAbsent(dateStr, dataPoint.dateTime)

            val entry = MeasurementEntry(
                time = timeStr,
                value = formatValue(dataPoint.value),
                timestamp = dataPoint.timestamp
            )

            dateToMeasurementsMap
                .getOrPut(dateStr) { mutableMapOf() }
                .getOrPut(series.seriesId) { mutableListOf() }
                .add(entry)
        }
    }

    /**
     * Formats a float value for display in the table
     */
    private fun formatValue(value: Float): String {
        // Remove decimal if it's a whole number
        return if (value % 1.0f == 0.0f) {
            value.toInt().toString()
        } else {
            String.format("%.1f", value)
        }
    }
}
