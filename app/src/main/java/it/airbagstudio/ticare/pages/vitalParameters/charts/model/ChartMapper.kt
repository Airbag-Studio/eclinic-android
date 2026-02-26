package it.airbagstudio.ticare.pages.vitalParameters.charts.model

import androidx.compose.ui.graphics.Color

import ch.ticare.eclinic.library.entity.VitalSignType
import ch.ticare.eclinic.library.entity.VitalSignsChartData
import ch.ticare.eclinic.library.entity.VitalSignsChartValue
import ch.ticare.eclinic.library.entity.VitalSignsChartsDataResponse
import it.airbagstudio.ticare.ui.theme.md_theme_light_primary
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.toDate
import java.util.Calendar

/**
 * Maps API response to chart configurations
 */
object ChartMapper {


    // Color palette for different vital signs
    private val typeColors = mapOf(
        1 to md_theme_light_primary,
        2 to Color(0xFFE53935),    // Red
        3 to Color(0xFF1E88E5),   // Blue
        4 to Color(0xFFFB8C00),    // Orange
        5 to Color(0xFF43A047),     // Green
        6 to Color(0xFF8E24AA)      // Purple
    )

    /**
     * Converts API response to list of chart configurations
     * Handles grouping of dual-line charts (e.g., blood pressure)
     */
    fun mapToChartConfigs(
        vitalSignTypes: List<VitalSignType>,
        response: VitalSignsChartsDataResponse?
    ): List<VitalSignChartConfig> {
        if (response == null || response.chartsData.isEmpty()) {
            return emptyList()
        }

        val chartDataMap = response.chartsData.groupBy { it.idType }
        val configs = mutableListOf<VitalSignChartConfig>()

        // Process based on order defined in chartsTypesOrder
        response.chartsTypesOrder.forEach { typeId ->
            vitalSignTypes.firstOrNull { it.id == typeId }?.let { vitalSignType ->
                val dataForType = chartDataMap[typeId] ?: return@forEach

                // Check if this is a dual-line chart (e.g., blood pressure)
                if (isDualLineType(typeId)) {
                    val config = createDualLineChart(vitalSignType, dataForType)
                    config?.let { configs.add(it) }
                } else {
                    // Single line chart
                    val config = createSingleLineChart(vitalSignType, dataForType.firstOrNull())
                    config?.let { configs.add(it) }
                }
            }

        }

        return configs
    }

    /**
     * Determines if a vital sign type should be rendered as dual-line
     */
    private fun isDualLineType(typeId: Int): Boolean {
        return typeId == 2 // Blood pressure has systolic/diastolic
    }

    /**
     * Creates a single-line chart configuration
     */
    private fun createSingleLineChart(
        vitalSignType:VitalSignType,
        chartData: VitalSignsChartData?
    ): VitalSignChartConfig? {


        val dataPoints = mapChartValues(chartData?.vitalSignsChartValues ?: listOf())

        val series = ChartLineSeries(
            seriesId = vitalSignType.id.toString(),
            label = vitalSignType.muSymbol ?: "",
            dataPoints = dataPoints,
            color = typeColors[vitalSignType.id] ?: md_theme_light_primary,
            showGradient = true,
            max = vitalSignType.max.toFloat(),
            min = vitalSignType.min.toFloat()
        )

        return VitalSignChartConfig(
            title = vitalSignType.desc ?: "Unknown",
            chartType = ChartType.SingleLine(),
            series = listOf(series),
            yAxisLabel =  vitalSignType.muSymbol
        )
    }

    /**
     * Creates a dual-line chart (e.g., blood pressure)
     */
    private fun createDualLineChart(
        vitalSignType:VitalSignType,
        chartDataList: List<VitalSignsChartData>
    ): VitalSignChartConfig? {
        // Find systolic and diastolic data based on valueIndex
        val systolicData = chartDataList.find { it.valueIndex == 1 } // Assuming 0 = systolic
        val diastolicData = chartDataList.find { it.valueIndex == 2 } // Assuming 1 = diastolic



        val systolicPoints = mapChartValues(systolicData?.vitalSignsChartValues ?: listOf())
        val diastolicPoints = mapChartValues(diastolicData?.vitalSignsChartValues ?: listOf())

        val systolicSeries = ChartLineSeries(
            seriesId = "systolic",
            label = "Sistolica",
            dataPoints = systolicPoints,
            color = Color.Red,
            showGradient = true,
            max = 90f,
            min = 60f
        )

        val diastolicSeries = ChartLineSeries(
            seriesId = "diastolic",
            label = "Diastolica",
            dataPoints = diastolicPoints,
            color = Color.Blue,
            showGradient = true,
            max = 160f,
            min = 105f
        )

        return VitalSignChartConfig(
            title = vitalSignType.desc ?: "",
            chartType = ChartType.DualLine(fillBetweenLines = true),
            series = listOf(systolicSeries, diastolicSeries),
            yAxisLabel = vitalSignType.muSymbol,
            yAxisRange = 60f..160f
        )
    }

    /**
     * Helper to map VitalSignsChartValue list to ChartDataPoint list
     */
    private fun mapChartValues(values: List<VitalSignsChartValue>): List<ChartDataPoint> {
        return values.mapNotNull { value ->
            val date = value.excDateTime.toDate("dd.MM.yyyy HH:mm") ?: return@mapNotNull null
            ChartDataPoint(
                timestamp = date.time,
                value = value.value.toFloat(),
                dateTime = date
            )
        }.sortedBy { it.timestamp }
    }

    /**
     * Filters chart configs by date based on the selected filter option.
     * Charts with no data points after filtering are excluded.
     */
    fun filterByDate(
        configs: List<VitalSignChartConfig>,
        filter: DateFilterOption
    ): List<VitalSignChartConfig> {
        val cutoffDate = when (filter) {
            DateFilterOption.ALL -> null
            DateFilterOption.LAST_15_DAYS -> {
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -15) }.time
            }
            DateFilterOption.LAST_WEEK -> {
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time
            }
        }

        if (cutoffDate == null) return configs

        return configs.mapNotNull { config ->
            val filteredSeries = config.series.map { series ->
                series.copy(
                    dataPoints = series.dataPoints.filter { it.dateTime.after(cutoffDate) }
                )
            }
            // Only keep configs that have at least one series with data points
            if (filteredSeries.any { it.dataPoints.isNotEmpty() }) {
                config.copy(series = filteredSeries)
            } else {
                null
            }
        }
    }
}
