package com.ohdodok.catchytape.feature.login

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ohdodok.catchytape.core.domain.signup.NicknameValidationResult
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

@BindingAdapter("nicknameValidationState")
fun TextView.bindNicknameValidationState(state: NicknameValidationResult) {
    val stateMessageMap = mapOf(
        NicknameValidationResult.EMPTY to "",
        NicknameValidationResult.VALID to "사용 가능한 닉네임이에요.",
        NicknameValidationResult.DUPLICATED to "이미 사용중인 닉네임이에요.",
        NicknameValidationResult.INVALID_LENGTH to "닉네임은 2~10글자까지 가능해요.",
        NicknameValidationResult.INVALID_CHARACTER to "한글, 영어, 특수문자(-, _, .)만 입력 가능해요.",
    )

    this.text = stateMessageMap[state] ?: ""
}