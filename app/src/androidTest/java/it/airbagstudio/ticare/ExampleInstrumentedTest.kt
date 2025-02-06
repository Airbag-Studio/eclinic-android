package it.airbagstudio.ticare

import android.webkit.URLUtil
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.net.URL
import ch.ticare.eclinic.library.entity.OperatingShift
import it.airbagstudio.ticare.utils.includeTime
import java.time.LocalTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    fun buildValidUrl(string: String): String? {
        val url =
            if (URLUtil.isNetworkUrl(string)) {
                URL(string)
            } else if (URLUtil.isNetworkUrl("https://$string")) {
                URL("https://$string")
            } else {
                null
            }

        url?.let { validUrl ->
            return "${validUrl.protocol}://${validUrl.host}:${validUrl.port}"
        }
        return null
    }

    @Test
    fun useAppContext() {
        val currentLocalDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val todayIndex = currentLocalDateTime.dayOfWeek.value - 1
        val monday = currentLocalDateTime.date.plus(- todayIndex,DateTimeUnit.DAY)
        val sunday = monday.plus(6,DateTimeUnit.DAY)
        print("$monday")

    }

    @Test
    fun testShift(){
        val shift = OperatingShift(
            name = "Pomeriggio",
            publicName = "Pomeriggio",
            publicShortName = "Pomeriggio",
            shortName = "Pomeriggio",
            startTime = "14:00",
            stopTime = "22:00"
        )
        assertTrue(shift.includeTime(LocalTime.parse("14:00")))
        assertTrue(shift.includeTime(LocalTime.parse("15:00")))
        assertTrue(shift.includeTime(LocalTime.parse("21:59")))
        assertFalse(shift.includeTime(LocalTime.parse("22:00")))
        assertFalse(shift.includeTime(LocalTime.parse("22:01")))
        assertFalse(shift.includeTime(LocalTime.parse("13:59")))
    }
}