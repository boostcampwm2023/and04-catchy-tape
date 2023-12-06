package com.ohdodok.catchytape.core.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

@BindingAdapter("list")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.bindItems(items: List<T>) {
    val adapter = this.adapter ?: return
    val listAdapter: ListAdapter<T, VH> = adapter as ListAdapter<T, VH>
    listAdapter.submitList(items)
}

@BindingAdapter("imgUrl")
fun ImageView.bindImg(url: String?) {
    if (url == null) return

    Glide.with(this.context)
        .load(url)
        .into(this)
}