package com.ohdodok.catchytape.feature.player

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("seconds")
fun TextView.bindMinutesAndSeconds(totalSeconds: Int) {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    text = resources.getString(R.string.minutes_and_seconds_format, minutes, seconds)
}