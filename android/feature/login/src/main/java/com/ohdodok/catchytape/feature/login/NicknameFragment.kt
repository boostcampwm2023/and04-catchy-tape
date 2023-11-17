package com.ohdodok.catchytape.feature.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.login.databinding.FragmentNicknameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NicknameFragment : BaseFragment<FragmentNicknameBinding>(R.layout.fragment_nickname) {

    private val args: NicknameFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAppbar()
        setUpStartBtn()
    }

    private fun setUpAppbar() {
        setupBackStack(binding.tbNickname)
    }

    private fun setUpStartBtn() {
        binding.btnStart.setOnClickListener {
            activity?.finish()
        }
    }
}