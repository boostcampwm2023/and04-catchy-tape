package com.ohdodok.catchytape.feature.playlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.playlist.databinding.FragmentPlayListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayListFragment : BaseFragment<FragmentPlayListBinding>(R.layout.fragment_play_list) {

    private val viewModel: PlayListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }

}