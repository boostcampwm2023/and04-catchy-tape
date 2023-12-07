package com.ohdodok.catchytape.feature.player

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.ui.R.drawable
import com.ohdodok.catchytape.core.ui.bindImg
import com.ohdodok.catchytape.feature.player.databinding.ViewPlayerControlBinding

class PlayerControlView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding: ViewPlayerControlBinding =
        ViewPlayerControlBinding.inflate(LayoutInflater.from(context), this, true)

    var music: Music? = null
        set(value) {
            field = value
            if (value == null) {
                binding.tvNoPlaylist.visibility = VISIBLE
            } else {
                binding.tvNoPlaylist.visibility = GONE
                binding.tvTitle.text = value.title
                binding.tvArtist.text = value.artist
                binding.ivThumbnail.bindImg(value.imageUrl)
            }
        }

    var isPlaying: Boolean = false
        set(value) {
            field = value
            binding.ibPlay.setImageDrawable(getPlayBtnDrawable())
        }

    var progress: Int = 0
        set(value) {
            field = value
            binding.lpiPlayerProgress.progress = value
        }

    var duration: Int = 0
        set(value) {
            field = value
            binding.lpiPlayerProgress.max = value
        }

    var nextEnabled: Boolean = false
        set(value) {
            field = value
            binding.ibNext.isEnabled = value
        }

    var playEnabled: Boolean = false
        set(value) {
            field = value
            binding.ibPlay.isEnabled = value
        }

    var previousEnabled: Boolean = false
        set(value) {
            field = value
            binding.ibPrevious.isEnabled = value
        }

    fun setOnPlayButtonClick(onPlayButtonClick: () -> Unit) {
        binding.ibPlay.setOnClickListener { onPlayButtonClick() }
    }

    private fun getPlayBtnDrawable(): Drawable? {
        return if (isPlaying) AppCompatResources.getDrawable(context, drawable.ic_pause)
        else AppCompatResources.getDrawable(context, drawable.ic_play)
    }

    fun setOnPreviousButtonClick(onPreviousButtonClick: () -> Unit) {
        binding.ibPrevious.setOnClickListener { onPreviousButtonClick() }
    }

    fun setOnNextButtonClick(onNextButtonClick: () -> Unit) {
        binding.ibNext.setOnClickListener { onNextButtonClick() }
    }
}