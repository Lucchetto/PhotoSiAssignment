package com.photosi.assignment.time

import android.content.Context
import java.util.concurrent.TimeUnit

object TimeFormatters {

    fun formatCountdown(context: Context, time: Long): String = with(context) {
        val hours = TimeUnit.MILLISECONDS.toHours(time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60

        buildString {
            if (hours > 0) append("${getString(R.string.hours_template, hours)} ")
            if (minutes > 0 || hours > 0) append("${getString(R.string.minutes_template, minutes)} ")
            append(getString(R.string.seconds_template, seconds))
        }
    }
}