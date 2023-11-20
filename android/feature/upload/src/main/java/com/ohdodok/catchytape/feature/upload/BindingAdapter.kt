package com.ohdodok.catchytape.feature.upload

import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter


@BindingAdapter("changeSelectedPosition")
fun AutoCompleteTextView.bindPosition(onChange: (Int) -> Unit) {
    setOnItemClickListener { _, _, position, _ -> onChange(position) }
}