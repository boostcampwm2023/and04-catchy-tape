package com.ohdodok.catchytape.feature.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.mypage.databinding.FragmentMyMusicsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMusicsFragment : BaseFragment<FragmentMyMusicsBinding>(R.layout.fragment_my_musics) {
    private val viewModel: MyMusicsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }
}