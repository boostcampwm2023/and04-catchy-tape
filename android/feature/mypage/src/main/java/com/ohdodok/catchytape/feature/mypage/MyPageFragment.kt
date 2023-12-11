package com.ohdodok.catchytape.feature.mypage

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.MusicAdapter
import com.ohdodok.catchytape.core.ui.Orientation
import com.ohdodok.catchytape.core.ui.RootViewInsetsCallback
import com.ohdodok.catchytape.core.ui.cterror.toMessageId
import com.ohdodok.catchytape.feature.mypage.databinding.FragmentMyPageBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewModel: MyPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, RootViewInsetsCallback())
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
        binding.btnUpload.setOnClickListener {
            val request =
                NavDeepLinkRequest.Builder.fromUri("android-app://com.ohdodok.catchytape/upload_fragment".toUri())
                    .build()
            findNavController().navigate(request)
        }
    }

    private fun setupRecyclerView() {
        binding.rvMusics.adapter = MusicAdapter(Orientation.VERTICAL)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchMyMusics(count = 3)
    }
}