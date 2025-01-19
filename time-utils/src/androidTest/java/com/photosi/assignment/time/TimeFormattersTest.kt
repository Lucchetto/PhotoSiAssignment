package com.photosi.assignment.time

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeFormattersInstrumentedTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun formatCountdown_timeWithHoursMinutesAndSeconds() {
        // Given
        val time = TimeUnit.HOURS.toMillis(69) +
                TimeUnit.MINUTES.toMillis(15) +
                TimeUnit.SECONDS.toMillis(30)

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("69h 15m 30s", result)
    }

    @Test
    fun formatCountdown_timeWithHoursAndSeconds_zeroMinutes() {
        // Given
        val time = TimeUnit.HOURS.toMillis(1) + TimeUnit.SECONDS.toMillis(5)

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("1h 0m 5s", result)
    }

    @Test
    fun formatCountdown_timeWithMinutesAndSecondsOnly() {
        // Given
        val time = TimeUnit.MINUTES.toMillis(15) + TimeUnit.SECONDS.toMillis(30)

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("15m 30s", result)
    }

    @Test
    fun formatCountdown_timeWithHoursOnly() {
        // Given
        val time = TimeUnit.HOURS.toMillis(1)

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("1h 0m 0s", result)
    }

    @Test
    fun formatCountdown_timeWithSecondsOnly() {
        // Given
        val time = TimeUnit.SECONDS.toMillis(45)

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("45s", result)
    }

    @Test
    fun formatCountdown_zeroTime() {
        // Given
        val time = 0L

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("0s", result)
    }

    @Test
    fun formatCountdown_edgeCase_oneMinute() {
        // Given
        val time = TimeUnit.MINUTES.toMillis(1)

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("1m 0s", result)
    }

    @Test
    fun formatCountdown_edgeCase_oneHour() {
        // Given
        val time = TimeUnit.HOURS.toMillis(1)

        // When
        val result = TimeFormatters.formatCountdown(context, time)

        // Then
        assertEquals("1h 0m 0s", result)
    }
}
