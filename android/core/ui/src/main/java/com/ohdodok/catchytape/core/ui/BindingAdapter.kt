package com.ohdodok.catchytape.core.ui

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


@BindingAdapter("submitList")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.bindItems(items: List<T>) {
    val adapter = this.adapter ?: return
    val listAdapter: ListAdapter<T, VH> = adapter as ListAdapter<T, VH>
    listAdapter.submitList(items)
}