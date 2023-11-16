package com.ohdodok.catchytape.core.ui

import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.google.android.material.appbar.MaterialToolbar


@BindingAdapter("pop_back_stack")
fun MaterialToolbar.bindBack(dummy: Any?) {
    setNavigationOnClickListener {
        findNavController().popBackStack()
    }
}


@BindingAdapter("music_title", "genre_position")
fun TextView.bindChangeEnable(musicTitle: String, genrePosition: Int) {
    if (musicTitle.isEmpty() || genrePosition == 0) {
        setTextColor(resources.getColor(R.color.on_surface_variant, context.theme))
        isEnabled = false
    } else {
        setTextColor(resources.getColor(R.color.on_surface, context.theme))
        isEnabled = true
    }
}


@BindingAdapter("change_selected_position")
fun AutoCompleteTextView.bindPosition(onChange: (Int) -> Unit) {
    setOnItemClickListener { _, _, position, _ -> onChange(position) }
}