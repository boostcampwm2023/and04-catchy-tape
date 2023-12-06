package com.ohdodok.catchytape.feature.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.core.ui.toMessageId
import com.ohdodok.catchytape.feature.search.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        observeEvents()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvSearch.adapter = MusicAdapter(Orientation.VERTICAL)
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is SearchEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }
                }
            }
        }
    }

}