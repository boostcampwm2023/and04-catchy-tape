package com.ohdodok.catchytape.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ohdodok.catchytape.core.ui.databinding.ItemPlaylistBinding
import com.ohdodok.catchytape.core.ui.model.PlaylistUiModel

class PlaylistAdapter :
    ListAdapter<PlaylistUiModel, PlaylistAdapter.PlaylistViewHolder>(PlaylistItemDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class PlaylistViewHolder private constructor(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaylistUiModel) {
            binding.playlist = item
        }

        companion object {
            fun from(parent: ViewGroup) = PlaylistViewHolder(
                ItemPlaylistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    object PlaylistItemDiffUtil : DiffUtil.ItemCallback<PlaylistUiModel>() {
        override fun areItemsTheSame(oldItem: PlaylistUiModel, newItem: PlaylistUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaylistUiModel, newItem: PlaylistUiModel): Boolean {
            return oldItem == newItem
        }
    }
}