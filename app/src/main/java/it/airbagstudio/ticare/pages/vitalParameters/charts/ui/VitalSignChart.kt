package it.airbagstudio.ticare.pages.vitalParameters.charts.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalBox
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.Fill
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.ChartDataPoint
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.ChartLineSeries
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.ChartType
import it.airbagstudio.ticare.pages.vitalParameters.charts.model.VitalSignChartConfig
import it.airbagstudio.ticare.utils.format
import java.util.Date

/**
 * Generic composable for rendering vital signs charts using Vico 3.0.0 API
 * Supports single line, dual line, and multi-line configurations
 *
 * @param config Chart configuration including data, type, and styling
 * @param modifier Optional modifier for the chart container
 */
@Composable
fun VitalSignChart(
    config: VitalSignChartConfig,
    modifier: Modifier = Modifier
) {
    var showFullscreen by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Chart Title with fullscreen button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = config.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Y-axis unit label
                    if (config.yAxisLabel.isNotEmpty()) {
                        Text(
                            text = config.yAxisLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { showFullscreen = true }) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Render chart
            VicoLineChart(config = config, isFullscreen = false)
        }
    }

    // Fullscreen dialog
    if (showFullscreen) {
        val context = LocalContext.current
        val activity = context as? ComponentActivity

        // Change orientation to sensor when fullscreen is shown
        DisposableEffect(Unit) {
            val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

            onDispose {
                // Restore original orientation when dialog is dismissed
                activity?.requestedOrientation = originalOrientation
            }
        }

        Dialog(
            onDismissRequest = { showFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = config.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (config.yAxisLabel.isNotEmpty()) {
                                Text(
                                    text = config.yAxisLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { showFullscreen = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fullscreen chart with zoom and scroll enabled
                    VicoLineChart(
                        config = config,
                        isFullscreen = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }

        }
    }
}

/**
 * Renders the line chart using Vico 3.0.0 API
 * @param config Chart configuration
 * @param isFullscreen Whether the chart is in fullscreen mode (enables zoom/scroll)
 * @param modifier Optional modifier
 */
@Composable
private fun VicoLineChart(
    config: VitalSignChartConfig,
    isFullscreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    // Collect all unique dates sorted by time and create index mapping
    val allDates = remember(config) {
        config.series
            .flatMap { it.dataPoints }
            .map { it.dateTime }
            .distinctBy { it.time }
            .sortedBy { it.time }
    }

    val indexToDate = remember(allDates) {
        allDates.mapIndexed { index, date -> index to date }.toMap()
    }

    // Convert chart data to Vico model using indices instead of timestamps
    LaunchedEffect(config, allDates) {
        if (config.series.flatMap { it.dataPoints }.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    config.series.filter { it.dataPoints.isNotEmpty() }.forEach { chartSeries ->
                        series(
                            x = chartSeries.dataPoints.map { point ->
                                // Find the index corresponding to this date
                                allDates.indexOfFirst { it.time == point.dateTime.time }.toDouble()
                            },
                            y = chartSeries.dataPoints.map { it.value.toDouble() }
                        )
                    }
                }
            }
        }
    }

    // Calculate Y-axis range provider
    val rangeProvider = if (config.yAxisRange != null) {
        CartesianLayerRangeProvider.fixed(
            minY = config.yAxisRange.start.toDouble(),
            maxY = config.yAxisRange.endInclusive.toDouble()
        )
    } else {
        CartesianLayerRangeProvider.auto()
    }

    // Create decorations for normal range (min-max area)
    val decorations = config.series.mapNotNull { series ->
        if (series.min != null && series.max != null) {
            val boxComponent = rememberShapeComponent(
                fill = Fill(series.color.copy(alpha = 0.15f))
            )
            HorizontalBox(
                y = { series.min.toDouble()..series.max.toDouble() },
                box = boxComponent
            )
        } else null
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                rangeProvider = rangeProvider,
                lineProvider = remember(config.series) {
                    LineCartesianLayer.LineProvider.series(
                        config.series.map { series ->
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(series.color))
                            )
                        }
                    )
                }
            ),
            startAxis = if (config.showYAxisLabels) {
                VerticalAxis.rememberStart()
            } else null,
            bottomAxis = if (config.showXAxisLabels) {
                HorizontalAxis.rememberBottom(
                    valueFormatter = remember(indexToDate) {
                        CartesianValueFormatter { _: CartesianMeasuringContext, value: Double, _: Axis.Position.Vertical? ->
                            val index = value.toInt()
                            indexToDate[index]?.format("dd/MM") ?: ""
                        }
                    }
                )
            } else null,
            decorations = decorations,
            getXStep = { 1.0 }  // Show label at each data point index
        ),
        placeholder = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Nessun dato disponibile")
                }
            }
        },
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = isFullscreen),
        zoomState = rememberVicoZoomState(
            zoomEnabled = isFullscreen,
            initialZoom = Zoom.Content
        ),
        modifier = if (isFullscreen) {
            modifier
        } else {
            Modifier
                .fillMaxWidth()
                .height(200.dp)
        }
    )
}

// ========================================
// Preview Functions
// ========================================
@Preview(name = "Single Line Chart - Peso", showBackground = true)
@Composable
private fun PreviewEmptyLineChart() {
    MaterialTheme {
        val now = System.currentTimeMillis()
        val dataPoints: List<ChartDataPoint> = listOf()

        val config = VitalSignChartConfig(
            title = "Peso",
            chartType = ChartType.DualLine(),
            series = listOf(
                ChartLineSeries(
                    seriesId = "weight",
                    label = "Peso",
                    dataPoints = dataPoints,
                    color = Color(0xFF2196F3),
                    max = null,
                    min = null
                ),
                ChartLineSeries(
                    seriesId = "weight",
                    label = "Peso",
                    dataPoints = dataPoints,
                    color = Color(0xFF2196F3),
                    max = null,
                    min = null
                )
            ),
            yAxisLabel = "kg",
            yAxisRange = 65f..75f
        )

        VitalSignChart(config = config)
    }
}
/**
 * Preview for SingleLine chart (e.g., Weight)
 */
@Preview(name = "Single Line Chart - Peso", showBackground = true)
@Composable
private fun PreviewSingleLineChart() {
    MaterialTheme {
        val now = System.currentTimeMillis()
        val dataPoints = listOf(
            ChartDataPoint(now - 7 * 24 * 60 * 60 * 1000, 68.5f, Date(now - 7 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 6 * 24 * 60 * 60 * 1000, 68.2f, Date(now - 6 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 5 * 24 * 60 * 60 * 1000, 68.8f, Date(now - 5 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 4 * 24 * 60 * 60 * 1000, 69.0f, Date(now - 4 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 3 * 24 * 60 * 60 * 1000, 68.7f, Date(now - 3 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 2 * 24 * 60 * 60 * 1000, 69.2f, Date(now - 2 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 69.5f, Date(now - 1 * 24 * 60 * 60 * 1000)),
        )

        val config = VitalSignChartConfig(
            title = "Peso",
            chartType = ChartType.SingleLine(),
            series = listOf(
                ChartLineSeries(
                    seriesId = "weight",
                    label = "Peso",
                    dataPoints = dataPoints,
                    color = Color(0xFF2196F3),
                    max = 75f,
                    min = 65f
                )
            ),
            yAxisLabel = "kg",
            yAxisRange = 65f..75f
        )

        VitalSignChart(config = config)
    }
}

/**
 * Preview for DualLine chart (e.g., Blood Pressure)
 */
@Preview(name = "Dual Line Chart - Pressione Arteriosa", showBackground = true)
@Composable
private fun PreviewDualLineChart() {
    MaterialTheme {
        val now = System.currentTimeMillis()
        val systolicData = listOf(
            ChartDataPoint(now - 7 * 24 * 60 * 60 * 1000, 125f, Date(now - 7 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 6 * 24 * 60 * 60 * 1000, 128f, Date(now - 6 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 5 * 24 * 60 * 60 * 1000, 122f, Date(now - 5 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 4 * 24 * 60 * 60 * 1000, 130f, Date(now - 4 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 3 * 24 * 60 * 60 * 1000, 126f, Date(now - 3 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 2 * 24 * 60 * 60 * 1000, 129f, Date(now - 2 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 127f, Date(now - 1 * 24 * 60 * 60 * 1000)),
        )

        val diastolicData = listOf(
            ChartDataPoint(now - 7 * 24 * 60 * 60 * 1000, 78f, Date(now - 7 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 6 * 24 * 60 * 60 * 1000, 80f, Date(now - 6 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 5 * 24 * 60 * 60 * 1000, 75f, Date(now - 5 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 4 * 24 * 60 * 60 * 1000, 82f, Date(now - 4 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 3 * 24 * 60 * 60 * 1000, 79f, Date(now - 3 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 2 * 24 * 60 * 60 * 1000, 81f, Date(now - 2 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 80f, Date(now - 1 * 24 * 60 * 60 * 1000)),
        )

        val config = VitalSignChartConfig(
            title = "Pressione Arteriosa",
            chartType = ChartType.DualLine(),
            series = listOf(
                ChartLineSeries(
                    seriesId = "systolic",
                    label = "Sistolica",
                    dataPoints = systolicData,
                    color = Color(0xFFF44336),
                    max = 140f,
                    min = 105f
                ),
                ChartLineSeries(
                    seriesId = "diastolic",
                    label = "Diastolica",
                    dataPoints = diastolicData,
                    color = Color(0xFF2196F3),
                    max = 90f,
                    min = 70f
                )
            ),
            yAxisLabel = "mmHg",
            yAxisRange = 70f..140f
        )

        VitalSignChart(config = config)
    }
}

/**
 * Preview for MultiLine chart (e.g., Multiple Vital Signs)
 */
@Preview(name = "Multi Line Chart - Parametri Vitali", showBackground = true)
@Composable
private fun PreviewMultiLineChart() {
    MaterialTheme {
        val now = System.currentTimeMillis()

        val tempData = listOf(
            ChartDataPoint(now - 5 * 24 * 60 * 60 * 1000, 36.5f, Date(now - 5 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 4 * 24 * 60 * 60 * 1000, 36.8f, Date(now - 4 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 3 * 24 * 60 * 60 * 1000, 37.2f, Date(now - 3 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 2 * 24 * 60 * 60 * 1000, 36.9f, Date(now - 2 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 36.7f, Date(now - 1 * 24 * 60 * 60 * 1000)),
        )

        val heartRateData = listOf(
            ChartDataPoint(now - 5 * 24 * 60 * 60 * 1000, 72f, Date(now - 5 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 4 * 24 * 60 * 60 * 1000, 75f, Date(now - 4 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 3 * 24 * 60 * 60 * 1000, 78f, Date(now - 3 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 2 * 24 * 60 * 60 * 1000, 74f, Date(now - 2 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 73f, Date(now - 1 * 24 * 60 * 60 * 1000)),
        )

        val saturationData = listOf(
            ChartDataPoint(now - 5 * 24 * 60 * 60 * 1000, 96f, Date(now - 5 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 4 * 24 * 60 * 60 * 1000, 97f, Date(now - 4 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 3 * 24 * 60 * 60 * 1000, 98f, Date(now - 3 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 2 * 24 * 60 * 60 * 1000, 97f, Date(now - 2 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 98f, Date(now - 1 * 24 * 60 * 60 * 1000)),
        )

        val config = VitalSignChartConfig(
            title = "Parametri Vitali",
            chartType = ChartType.MultiLine(),
            series = listOf(
                ChartLineSeries(
                    seriesId = "temperature",
                    label = "Temperatura",
                    dataPoints = tempData,
                    color = Color(0xFFFF9800),
                    max = 37.5f,
                    min = 36.0f
                ),
                ChartLineSeries(
                    seriesId = "heartRate",
                    label = "Battito Cardiaco",
                    dataPoints = heartRateData,
                    color = Color(0xFFE91E63),
                    max = 100f,
                    min = 60f
                ),
                ChartLineSeries(
                    seriesId = "saturation",
                    label = "Saturazione",
                    dataPoints = saturationData,
                    color = Color(0xFF4CAF50),
                    max = 100f,
                    min = 95f
                )
            ),
            yAxisLabel = "Valori",
            yAxisRange = 30f..100f
        )

        VitalSignChart(config = config)
    }
}

/**
 * Preview with minimal data (2 points)
 */
@Preview(name = "Minimal Data - 2 Points", showBackground = true)
@Composable
private fun PreviewMinimalData() {
    MaterialTheme {
        val now = System.currentTimeMillis()
        val dataPoints = listOf(
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 95f, Date(now - 1 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now, 97f, Date(now)),
        )

        val config = VitalSignChartConfig(
            title = "Saturazione O2",
            chartType = ChartType.SingleLine(),
            series = listOf(
                ChartLineSeries(
                    seriesId = "saturation",
                    label = "SpO2",
                    dataPoints = dataPoints,
                    color = Color(0xFF00BCD4),
                    max = 100f,
                    min = 95f
                )
            ),
            yAxisLabel = "%"
        )

        VitalSignChart(config = config)
    }
}

/**
 * Preview without axis labels
 */
@Preview(name = "No Axis Labels", showBackground = true)
@Composable
private fun PreviewNoAxisLabels() {
    MaterialTheme {
        val now = System.currentTimeMillis()
        val dataPoints = listOf(
            ChartDataPoint(now - 4 * 24 * 60 * 60 * 1000, 36.5f, Date(now - 4 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 3 * 24 * 60 * 60 * 1000, 36.8f, Date(now - 3 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 2 * 24 * 60 * 60 * 1000, 37.1f, Date(now - 2 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now - 1 * 24 * 60 * 60 * 1000, 36.9f, Date(now - 1 * 24 * 60 * 60 * 1000)),
            ChartDataPoint(now, 36.7f, Date(now)),
        )

        val config = VitalSignChartConfig(
            title = "Temperatura Corporea",
            chartType = ChartType.SingleLine(),
            series = listOf(
                ChartLineSeries(
                    seriesId = "temperature",
                    label = "Temperatura",
                    dataPoints = dataPoints,
                    color = Color(0xFFFF5722),
                    max = 37.5f,
                    min = 36.0f
                )
            ),
            yAxisLabel = "°C",
            showXAxisLabels = false,
            showYAxisLabels = false
        )

        VitalSignChart(config = config)
    }
}