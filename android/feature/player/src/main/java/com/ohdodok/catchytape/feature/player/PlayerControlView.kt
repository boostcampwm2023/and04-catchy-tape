package com.ohdodok.catchytape.feature.player

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.ohdodok.catchytape.core.ui.R.*
import com.ohdodok.catchytape.core.ui.bindImg
import kotlin.properties.Delegates

class PlayerControlView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private lateinit var thumbnailUrl: String
    private lateinit var title: String
    private lateinit var artist: String
    private var isPlaying: Boolean by Delegates.notNull()
    private var progress: Int by Delegates.notNull()
    private var duration: Int by Delegates.notNull()

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
                isPlaying = getBoolean(R.styleable.PlayerBarView_isPlaying, false)
                progress = getInt(R.styleable.PlayerBarView_progress, 0)
                duration = getInt(R.styleable.PlayerBarView_duration, 0)
            } finally {
                recycle()
            }
        }
    }

    private fun initView() {
        inflate(context, R.layout.view_player_control, this)

        val thumbnailView: ImageView = findViewById(R.id.iv_thumbnail)
        val titleView: TextView = findViewById(R.id.tv_title)
        val artistView: TextView = findViewById(R.id.tv_artist)
        val playButton: ImageButton = findViewById(R.id.ib_play)
        val indicator: LinearProgressIndicator = findViewById(R.id.lpi_player_progress)

        thumbnailView.bindImg(thumbnailUrl)

        titleView.text = title
        artistView.text = artist

        val buttonDrawable =
            if (isPlaying) AppCompatResources.getDrawable(context, drawable.ic_pause)
            else AppCompatResources.getDrawable(context, drawable.ic_play)
        playButton.setImageDrawable(buttonDrawable)

        indicator.progress = progress
        indicator.max = duration
    }

    fun setOnPlayButtonClick(onPlayButtonClick: () -> Unit) {
        val playButton: ImageButton = findViewById(R.id.ib_play)

        playButton.setOnClickListener { onPlayButtonClick() }
    }
}