package com.ohdodok.catchytape.feature.mypage

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.core.ui.RootViewInsetsCallback
import com.ohdodok.catchytape.core.ui.toMessageId
import com.ohdodok.catchytape.feature.mypage.databinding.FragmentMyMusicsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMusicsFragment : BaseFragment<FragmentMyMusicsBinding>(R.layout.fragment_my_musics) {
    private val viewModel: MyMusicsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root, RootViewInsetsCallback())
        binding.viewModel = viewModel

        setupRecyclerView()
        observeEvents()
        setupBackStack(binding.tbMyMusics)
    }

    private fun setupRecyclerView() {
        binding.rvMyMusics.adapter = MusicAdapter(Orientation.VERTICAL)
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is MyMusicsEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }
                }
            }
        }
    }
}