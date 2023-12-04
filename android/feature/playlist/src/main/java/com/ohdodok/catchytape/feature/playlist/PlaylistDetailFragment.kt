package com.ohdodok.catchytape.feature.playlist

import android.os.Bundle
import android.view.View
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.playlist.databinding.FragmentPlaylistDetailBinding

class PlaylistDetailFragment :
    BaseFragment<FragmentPlaylistDetailBinding>(R.layout.fragment_playlist_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackStack(binding.tbPlaylistDetail)
    }

}

