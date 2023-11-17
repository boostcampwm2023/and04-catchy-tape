package com.ohdodok.catchytape.feature.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.login.databinding.FragmentNicknameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NicknameFragment : BaseFragment<FragmentNicknameBinding>(R.layout.fragment_nickname) {

    private val args: NicknameFragmentArgs by navArgs()
    private val viewModel: NicknameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setUpAppbar()
        setUpStartBtn()
        observeEvents()
    }

    private fun setUpAppbar() {
        setupBackStack(binding.tbNickname)
    }

    private fun setUpStartBtn() {
        binding.btnStart.setOnClickListener {
            viewModel.signUp(args.googleToken)
        }
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is NicknameEvent.NavigateToHome -> {
                        activity?.finish()
                    }
                }
            }
        }
    }
}