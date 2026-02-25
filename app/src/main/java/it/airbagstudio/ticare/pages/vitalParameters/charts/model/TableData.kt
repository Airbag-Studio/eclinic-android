package it.airbagstudio.ticare.pages.vitalParameters.charts.model

import java.util.Date

/**
 * Represents a single measurement entry within a table cell
 */
data class MeasurementEntry(
    val time: String,      // "HH:mm" format
    val value: String,     // "79" for single values or "120/80" for blood pressure
    val timestamp: Long    // For sorting multiple entries
)

/**
 * Represents a cell in the table containing one or more measurements
 */
data class TableCell(
    val measurements: List<MeasurementEntry>
) {
    /**
     * Returns true if cell has no measurements
     */
    fun isEmpty(): Boolean = measurements.isEmpty()
}

/**
 * Represents a column in the vital signs table
 */
data class TableColumn(
    val vitalSignId: String,              // Unique identifier for the vital sign
    val title: String,                     // Display name (e.g., "Peso", "Pressione Arteriosa")
    val unit: String,                      // Unit of measurement (e.g., "kg", "mmHg", "%")
    val isDualValue: Boolean = false       // true for blood pressure (systolic/diastolic)
)

/**
 * Represents a row in the vital signs table (one row per date)
 */
data class TableRow(
    val date: String,                      // "dd.MM.yyyy" format
    val dateObj: Date,                     // Date object for sorting
    val cells: Map<String, TableCell>      // Map from vitalSignId to TableCell
)

/**
 * Complete table data structure for vital signs
 */
data class VitalSignsTableData(
    val columns: List<TableColumn>,        // Table columns (excluding date column)
    val rows: List<TableRow>               // Table rows sorted by date (descending)
) {
    /**
     * Returns true if table has no data to display
     */
    fun isEmpty(): Boolean = columns.isEmpty() || rows.isEmpty()
}
