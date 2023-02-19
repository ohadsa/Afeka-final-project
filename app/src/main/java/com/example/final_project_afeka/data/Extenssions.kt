package com.example.final_project_afeka.data

import java.util.concurrent.TimeUnit


fun Long.durationFromTime(): Duration {
    val delta = System.currentTimeMillis() - this
    val hours = TimeUnit.MILLISECONDS.toHours(delta)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(delta - TimeUnit.HOURS.toMillis(hours))
    val seconds =
        TimeUnit.MILLISECONDS.toSeconds(delta - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(
            minutes))
    return Duration(hours, minutes, seconds)
}
