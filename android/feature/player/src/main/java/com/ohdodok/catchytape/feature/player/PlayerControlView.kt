package com.ohdodok.catchytape.feature.player

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.ohdodok.catchytape.core.ui.R.drawable
import com.ohdodok.catchytape.core.ui.bindImg
import com.ohdodok.catchytape.feature.player.databinding.ViewPlayerControlBinding

class PlayerControlView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding: ViewPlayerControlBinding =
        ViewPlayerControlBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var thumbnailUrl: String
    private lateinit var title: String
    private lateinit var artist: String

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

    init {
        initAttrs(attrs)
        initView()
    }


    private fun initAttrs(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlayerBarView,
            0, 0
        ).apply {
            try {
                thumbnailUrl = getString(R.styleable.PlayerBarView_thumbnailUrl) ?: ""
                title = getString(R.styleable.PlayerBarView_title) ?: ""
                artist = getString(R.styleable.PlayerBarView_artist) ?: ""
                isPlaying = getBoolean(R.styleable.PlayerBarView_isPlaying, false)
                progress = getInt(R.styleable.PlayerBarView_progress, 0)
                duration = getInt(R.styleable.PlayerBarView_duration, 0)
            } finally {
                recycle()
            }
        }
    }

    private fun initView() {
        binding.ivThumbnail.bindImg(thumbnailUrl)
        binding.tvTitle.text = title
        binding.tvArtist.text = artist
    }


    fun setOnPlayButtonClick(onPlayButtonClick: () -> Unit) {
        binding.ibPlay.setOnClickListener { onPlayButtonClick() }
    }

    private fun getPlayBtnDrawable(): Drawable? {
        return if (isPlaying) AppCompatResources.getDrawable(context, drawable.ic_pause)
        else AppCompatResources.getDrawable(context, drawable.ic_play)
    }

}