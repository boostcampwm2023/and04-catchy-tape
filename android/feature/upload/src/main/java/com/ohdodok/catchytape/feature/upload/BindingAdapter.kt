package com.ohdodok.catchytape.feature.upload

import android.R
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter

@BindingAdapter("list")
fun AutoCompleteTextView.setAdapter(list: List<String>) {
    val adapter = ArrayAdapter(this.context, R.layout.simple_list_item_1, list)
    setAdapter(adapter)
}