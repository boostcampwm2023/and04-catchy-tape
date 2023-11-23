package com.ohdodok.catchytape.feature.home

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.feature.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.rvRecentlyAddedSong.adapter = MusicAdapter(musicItemOrientation = Orientation.HORIZONTAL)

        binding.ibUpload.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://com.ohdodok.catchytape/upload_fragment".toUri())
                .build()
            findNavController().navigate(request)
        }
    }
}