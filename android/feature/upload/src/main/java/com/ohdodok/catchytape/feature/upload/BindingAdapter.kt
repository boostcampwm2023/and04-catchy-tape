package com.ohdodok.catchytape.feature.upload

import android.R
import android.graphics.drawable.Drawable
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator

@BindingAdapter("list")
fun AutoCompleteTextView.setAdapter(list: List<String>) {
    val adapter = ArrayAdapter(this.context, R.layout.simple_list_item_1, list)
    setAdapter(adapter)
}

@BindingAdapter("visible")
fun LinearProgressIndicator.setVisible(uiState: UploadUiState) {
    isVisible = uiState.audioState is InputState.Loading || uiState.imageState is InputState.Loading
}

@BindingAdapter("completeBtnEnable")
fun Button.setCompleteBtnEnable(uiState: UploadUiState) {
    isEnabled =
        uiState.audioState is InputState.Success
                && uiState.imageState is InputState.Success
                && uiState.titleState is InputState.Success
                && uiState.genreState is InputState.Success
}

@BindingAdapter("uploadedThumbnail")
fun ImageView.bindUrl(uiState: UploadUiState) {
    if (uiState.imageState is InputState.Success) {
        Glide.with(this)
            .load(uiState.imageState.value)
            .into(this)
    }
}

@BindingAdapter("visible")
fun ImageView.setVisible(uiState: UploadUiState) {
    isVisible = uiState.imageState is InputState.Empty
}