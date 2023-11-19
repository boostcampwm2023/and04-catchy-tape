package com.ohdodok.catchytape.core.ui.bindingadapter

import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter


@BindingAdapter("changeSelectedPosition")
fun AutoCompleteTextView.bindPosition(onChange: (Int) -> Unit) {
    setOnItemClickListener { _, _, position, _ -> onChange(position) }
}