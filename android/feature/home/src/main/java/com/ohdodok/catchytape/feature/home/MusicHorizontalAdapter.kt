package com.ohdodok.catchytape.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.feature.home.databinding.ItemMusicHorizontalBinding

class MusicHorizontalAdapter :
    ListAdapter<Music, MusicHorizontalAdapter.MusicHorizontalViewHolder>(MusicDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHorizontalViewHolder {
        return MusicHorizontalViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MusicHorizontalViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class MusicHorizontalViewHolder private constructor(
        private val binding: ItemMusicHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Music) {
            binding.music = item
        }

        companion object {
            fun from(parent: ViewGroup): MusicHorizontalViewHolder {
                return MusicHorizontalViewHolder(
                    ItemMusicHorizontalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

object MusicDiffUtil : DiffUtil.ItemCallback<Music>() {
    override fun areItemsTheSame(oldItem: Music, newItem: Music) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Music, newItem: Music) =
        oldItem == newItem
}