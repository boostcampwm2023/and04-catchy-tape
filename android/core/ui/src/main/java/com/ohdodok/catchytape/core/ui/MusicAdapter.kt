package com.ohdodok.catchytape.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.ui.databinding.ItemMusicHorizontalBinding
import com.ohdodok.catchytape.core.ui.databinding.ItemMusicVerticalBinding


class MusicAdapter(
    private val musicItemOrientation: Orientation,
    private val listener: Listener = Listener {  }, // todo : 클릭 이벤트 구현이 완료되면 디폴트 값을 지워주세요.
) : ListAdapter<Music, RecyclerView.ViewHolder>(MusicDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (musicItemOrientation) {
            Orientation.HORIZONTAL -> HorizontalViewHolder.from(parent)
            Orientation.VERTICAL -> VerticalViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HorizontalViewHolder -> holder.bind(currentList[position])
            is VerticalViewHolder -> holder.bind(currentList[position])
        }
    }


    class HorizontalViewHolder private constructor(private val binding: ItemMusicHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Music) {
            binding.music = item
        }

        companion object {
            fun from(parent: ViewGroup) = HorizontalViewHolder(
                ItemMusicHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    class VerticalViewHolder private constructor(private val binding: ItemMusicVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Music) {
            binding.music = item
        }

        companion object {
            fun from(parent: ViewGroup) = VerticalViewHolder(
                ItemMusicVerticalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    fun interface Listener {
        fun onClick(music: Music)
    }
}

object MusicDiffUtil : DiffUtil.ItemCallback<Music>() {
    override fun areItemsTheSame(oldItem: Music, newItem: Music) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Music, newItem: Music) =
        oldItem == newItem
}