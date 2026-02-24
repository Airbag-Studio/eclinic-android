package it.airbagstudio.ticare.pages.vitalParameters.charts.model

import androidx.compose.ui.graphics.Color
import ch.ticare.eclinic.library.entity.VitalSignsChartData
import ch.ticare.eclinic.library.entity.VitalSignsChartValue
import ch.ticare.eclinic.library.entity.VitalSignsChartsDataResponse
import it.airbagstudio.ticare.ui.theme.md_theme_light_primary
import it.airbagstudio.ticare.utils.SERVER_PARAMETER_DATE_TIME_FORMAT
import it.airbagstudio.ticare.utils.toDate

/**
 * Maps API response to chart configurations
 */
object ChartMapper {

    // Known vital sign type IDs (these match backend definitions)
    private const val TYPE_WEIGHT = 1
    private const val TYPE_BP_SYSTOLIC = 2
    private const val TYPE_BP_DIASTOLIC = 3
    private const val TYPE_TEMPERATURE = 4
    private const val TYPE_SATURATION = 5
    private const val TYPE_HEART_RATE = 6
    // Add more as needed...

    // Mapping of vital sign types to display names (could be localized)
    private val typeNames = mapOf(
        TYPE_WEIGHT to "Peso",
        TYPE_BP_SYSTOLIC to "Pressione Arteriosa",
        TYPE_TEMPERATURE to "Temperatura",
        TYPE_SATURATION to "Saturazione",
        TYPE_HEART_RATE to "Frequenza Cardiaca"
    )

    // Mapping of vital sign types to units
    private val typeUnits = mapOf(
        TYPE_WEIGHT to "kg",
        TYPE_BP_SYSTOLIC to "mmHg",
        TYPE_TEMPERATURE to "°C",
        TYPE_SATURATION to "%",
        TYPE_HEART_RATE to "bpm"
    )

    // Color palette for different vital signs
    private val typeColors = mapOf(
        TYPE_WEIGHT to md_theme_light_primary,
        TYPE_BP_SYSTOLIC to Color(0xFFE53935),    // Red
        TYPE_BP_DIASTOLIC to Color(0xFF1E88E5),   // Blue
        TYPE_TEMPERATURE to Color(0xFFFB8C00),    // Orange
        TYPE_SATURATION to Color(0xFF43A047),     // Green
        TYPE_HEART_RATE to Color(0xFF8E24AA)      // Purple
    )

    /**
     * Converts API response to list of chart configurations
     * Handles grouping of dual-line charts (e.g., blood pressure)
     */
    fun mapToChartConfigs(
        response: VitalSignsChartsDataResponse?
    ): List<VitalSignChartConfig> {
        if (response == null || response.chartsData.isEmpty()) {
            return emptyList()
        }

        val chartDataMap = response.chartsData.groupBy { it.idType }
        val configs = mutableListOf<VitalSignChartConfig>()

        // Process based on order defined in chartsTypesOrder
        response.chartsTypesOrder.forEach { typeId ->
            val dataForType = chartDataMap[typeId] ?: return@forEach

            // Check if this is a dual-line chart (e.g., blood pressure)
            if (isDualLineType(typeId)) {
                val config = createDualLineChart(typeId, dataForType)
                config?.let { configs.add(it) }
            } else {
                // Single line chart
                val config = createSingleLineChart(typeId, dataForType.firstOrNull())
                config?.let { configs.add(it) }
            }
        }

        return configs
    }

    /**
     * Determines if a vital sign type should be rendered as dual-line
     */
    private fun isDualLineType(typeId: Int): Boolean {
        return typeId == TYPE_BP_SYSTOLIC // Blood pressure has systolic/diastolic
    }

    /**
     * Creates a single-line chart configuration
     */
    private fun createSingleLineChart(
        typeId: Int,
        chartData: VitalSignsChartData?
    ): VitalSignChartConfig? {
        if (chartData == null || chartData.vitalSignsChartValues.isEmpty()) {
            return null
        }

        val dataPoints = mapChartValues(chartData.vitalSignsChartValues)

        if (dataPoints.isEmpty()) return null

        val series = ChartLineSeries(
            seriesId = typeId.toString(),
            label = typeNames[typeId] ?: "Unknown",
            dataPoints = dataPoints,
            color = typeColors[typeId] ?: md_theme_light_primary,
            showGradient = true
        )

        return VitalSignChartConfig(
            title = typeNames[typeId] ?: "Unknown",
            chartType = ChartType.SingleLine(),
            series = listOf(series),
            yAxisLabel = typeUnits[typeId] ?: ""
        )
    }

    /**
     * Creates a dual-line chart (e.g., blood pressure)
     */
    private fun createDualLineChart(
        typeId: Int,
        chartDataList: List<VitalSignsChartData>
    ): VitalSignChartConfig? {
        // Find systolic and diastolic data based on valueIndex
        val systolicData = chartDataList.find { it.valueIndex == 0 } // Assuming 0 = systolic
        val diastolicData = chartDataList.find { it.valueIndex == 1 } // Assuming 1 = diastolic

        if (systolicData == null || diastolicData == null) {
            return null
        }

        val systolicPoints = mapChartValues(systolicData.vitalSignsChartValues)
        val diastolicPoints = mapChartValues(diastolicData.vitalSignsChartValues)

        if (systolicPoints.isEmpty() || diastolicPoints.isEmpty()) {
            return null
        }

        val systolicSeries = ChartLineSeries(
            seriesId = "systolic",
            label = "Sistolica",
            dataPoints = systolicPoints,
            color = typeColors[TYPE_BP_SYSTOLIC] ?: Color.Red,
            showGradient = true
        )

        val diastolicSeries = ChartLineSeries(
            seriesId = "diastolic",
            label = "Diastolica",
            dataPoints = diastolicPoints,
            color = typeColors[TYPE_BP_DIASTOLIC] ?: Color.Blue,
            showGradient = true
        )

        return VitalSignChartConfig(
            title = typeNames[typeId] ?: "Pressione Arteriosa",
            chartType = ChartType.DualLine(fillBetweenLines = true),
            series = listOf(systolicSeries, diastolicSeries),
            yAxisLabel = typeUnits[typeId] ?: "mmHg"
        )
    }

    /**
     * Helper to map VitalSignsChartValue list to ChartDataPoint list
     */
    private fun mapChartValues(values: List<VitalSignsChartValue>): List<ChartDataPoint> {
        return values.mapNotNull { value ->
            val date = value.excDateTime.toDate(SERVER_PARAMETER_DATE_TIME_FORMAT) ?: return@mapNotNull null
            ChartDataPoint(
                timestamp = date.time,
                value = value.value.toFloat(),
                dateTime = date
            )
        }.sortedBy { it.timestamp }
    }
}
