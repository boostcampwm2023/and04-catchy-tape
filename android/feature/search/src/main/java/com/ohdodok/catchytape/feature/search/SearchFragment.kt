package com.ohdodok.catchytape.feature.search

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.core.ui.RootViewInsetsCallback
import com.ohdodok.catchytape.core.ui.cterror.toMessageId
import com.ohdodok.catchytape.feature.search.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root, RootViewInsetsCallback())
        binding.viewModel = viewModel
        observeEvents()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvSearch.adapter = MusicAdapter(
            musicItemOrientation = Orientation.VERTICAL,
            listener = viewModel
        )
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is SearchEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }

                    is SearchEvent.NavigateToPlayerScreen -> {
                        findNavController().navigateToPlayerScreen()
                    }
                }
            }
        }
    }

}


private fun NavController.navigateToPlayerScreen() {
    val request =
        NavDeepLinkRequest.Builder.fromUri("android-app://com.ohdodok.catchytape/player_fragment".toUri())
            .build()

    navigate(request)
}