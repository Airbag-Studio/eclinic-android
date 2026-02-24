package it.airbagstudio.ticare.pages.vitalParameters.charts.model

import androidx.compose.ui.graphics.Color
import java.util.Date

/**
 * Represents a single data point in a chart series
 */
data class ChartDataPoint(
    val timestamp: Long,        // Unix timestamp for x-axis
    val value: Float,           // Y-axis value
    val dateTime: Date         // For formatting labels
)

/**
 * Represents a line series in the chart
 */
data class ChartLineSeries(
    val seriesId: String,                    // Unique identifier (e.g., "systolic", "diastolic")
    val label: String,                       // Display name (e.g., "Sistolica", "Diastolica")
    val dataPoints: List<ChartDataPoint>,    // Sorted by timestamp
    val color: Color,                        // Line color
    val showGradient: Boolean = true         // Whether to show gradient fill
)

/**
 * Sealed class defining different chart types
 */
sealed class ChartType {
    /**
     * Single line chart with gradient fill underneath
     * Used for: Weight, Saturation, Temperature, etc.
     */
    data class SingleLine(
        val fillGradientAlpha: Float = 0.3f
    ) : ChartType()

    /**
     * Dual line chart with gradient fill between lines
     * Used for: Blood Pressure (systolic/diastolic)
     */
    data class DualLine(
        val fillBetweenLines: Boolean = true,
        val fillGradientAlpha: Float = 0.3f
    ) : ChartType()

    /**
     * Multiple line chart (3+ lines)
     * Each line has its own gradient
     */
    data class MultiLine(
        val maxLines: Int = 5
    ) : ChartType()
}

/**
 * Main configuration class for vital signs charts
 */
data class VitalSignChartConfig(
    val title: String,                          // Chart title (e.g., "Peso", "Pressione Arteriosa")
    val chartType: ChartType,                   // Type of chart to render
    val series: List<ChartLineSeries>,          // 1 or more data series
    val yAxisLabel: String,                     // Y-axis unit (e.g., "kg", "mmHg", "%")
    val yAxisRange: ClosedFloatingPointRange<Float>? = null,  // Optional fixed range
    val showGrid: Boolean = true,               // Show grid lines
    val showXAxisLabels: Boolean = true,        // Show date labels on x-axis
    val showYAxisLabels: Boolean = true         // Show value labels on y-axis
) {
    init {
        require(series.isNotEmpty()) { "Chart must have at least one series" }

        when (chartType) {
            is ChartType.SingleLine -> require(series.size == 1) {
                "SingleLine chart requires exactly 1 series"
            }
            is ChartType.DualLine -> require(series.size == 2) {
                "DualLine chart requires exactly 2 series"
            }
            is ChartType.MultiLine -> require(series.size >= 3) {
                "MultiLine chart requires at least 3 series"
            }
        }
    }
}

/**
 * UI state for chart display
 */
sealed class ChartUiState {
    object Loading : ChartUiState()
    data class Success(val charts: List<VitalSignChartConfig>) : ChartUiState()
    data class Empty(val message: String = "Nessun dato disponibile") : ChartUiState()
    data class Error(val message: String) : ChartUiState()
}
