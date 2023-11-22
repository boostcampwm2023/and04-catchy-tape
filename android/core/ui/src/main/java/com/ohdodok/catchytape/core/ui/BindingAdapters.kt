package com.ohdodok.catchytape.core.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun ImageView.bindImg(url: String) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}