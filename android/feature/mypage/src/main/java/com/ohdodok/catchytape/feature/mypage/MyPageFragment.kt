package com.ohdodok.catchytape.feature.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.core.ui.toMessageId
import com.ohdodok.catchytape.feature.mypage.databinding.FragmentMyPageBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel: MyPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        observeEvents()
        setupButtons()
        setupRecyclerView()
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is MyPageEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnMore.setOnClickListener {
            val action = MyPageFragmentDirections.actionMyPageFragmentToMyMusicsFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        binding.rvMusics.adapter = MusicAdapter(Orientation.VERTICAL)
    }
}