package com.ohdodok.catchytape.feature.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ohdodok.catchytape.core.domain.model.PlayListItem
import com.ohdodok.catchytape.feature.playlist.databinding.ItemPlayListBinding

class PlayListAdapter : ListAdapter<PlayListItem, RecyclerView.ViewHolder>(PlayListItemDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlayListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PlayListViewHolder).bind(currentList[position])
    }

    class PlayListViewHolder private constructor(private val binding: ItemPlayListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlayListItem) {
            binding.playListItem = item
        }

        companion object {
            fun from(parent: ViewGroup) = PlayListViewHolder(
                ItemPlayListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    object PlayListItemDiffUtil : DiffUtil.ItemCallback<PlayListItem>() {
        override fun areItemsTheSame(oldItem: PlayListItem, newItem: PlayListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlayListItem, newItem: PlayListItem): Boolean {
            return oldItem == newItem
        }
    }
}