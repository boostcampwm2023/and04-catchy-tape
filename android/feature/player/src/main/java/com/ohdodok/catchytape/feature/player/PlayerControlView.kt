package com.ohdodok.catchytape.feature.player

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.ohdodok.catchytape.core.ui.R.drawable
import com.ohdodok.catchytape.core.ui.bindImg
import com.ohdodok.catchytape.feature.player.databinding.ViewPlayerControlBinding
import kotlin.properties.Delegates

class PlayerControlView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private lateinit var thumbnailUrl: String
    private lateinit var title: String
    private lateinit var artist: String
    private var _isPlaying: Boolean by Delegates.notNull()
    private var _progress: Int by Delegates.notNull()
    private var _duration: Int by Delegates.notNull()
    private lateinit var playButton: ImageButton
    private lateinit var progressIndicator: LinearProgressIndicator

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
                title = getString(R.styleable.PlayerBarView_title) ?: "" // 썸네일
                artist = getString(R.styleable.PlayerBarView_artist) ?: ""
                _isPlaying = getBoolean(R.styleable.PlayerBarView_isPlaying, false)
                _progress = getInt(R.styleable.PlayerBarView_progress, 0)
                _duration = getInt(R.styleable.PlayerBarView_duration, 0)
            } finally {
                recycle()
            }
        }
    }

    private fun initView() {
        val binding = ViewPlayerControlBinding.inflate(LayoutInflater.from(context), this, true)

        val thumbnailView = binding.ivThumbnail
        playButton = binding.ibPlay
        progressIndicator = binding.lpiPlayerProgress

        thumbnailView.bindImg(thumbnailUrl)
        binding.tvTitle.text = title
        binding.tvArtist.text = artist
    }


    fun setOnPlayButtonClick(onPlayButtonClick: () -> Unit) {
        playButton.setOnClickListener { onPlayButtonClick() }
    }

    private fun getPlayBtnDrawable(): Drawable? {
        return if (_isPlaying) AppCompatResources.getDrawable(context, drawable.ic_pause)
        else AppCompatResources.getDrawable(context, drawable.ic_play)
    }


    fun setIsPlaying(isPlaying: Boolean) {
        _isPlaying = isPlaying
        playButton.setImageDrawable(getPlayBtnDrawable())
    }

    fun setProgress(progress: Int) {
        progressIndicator.progress = progress
        _progress = progress
    }

    fun setDuration(duration: Int) {
        progressIndicator.max = duration
        _duration = duration
    }


}