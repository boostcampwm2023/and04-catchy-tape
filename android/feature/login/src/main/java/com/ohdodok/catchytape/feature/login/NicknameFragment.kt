package com.ohdodok.catchytape.feature.login

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ohdodok.catchytape.core.domain.usecase.signup.NicknameValidationResult
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
                        val intent = Intent()
                        intent.component = ComponentName("com.ohdodok.catchytape", "com.ohdodok.catchytape.MainActivity")
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        }
    }
}

@BindingAdapter("nicknameValidationState")
fun TextView.bindNicknameValidationState(state: NicknameValidationResult) {
    val messageId = when(state) {
        NicknameValidationResult.EMPTY -> R.string.empty_nickname
        NicknameValidationResult.VALID -> R.string.valid_nickname
        NicknameValidationResult.DUPLICATED -> R.string.duplicated_nickname
        NicknameValidationResult.INVALID_LENGTH -> R.string.invalid_length
        NicknameValidationResult.INVALID_CHARACTER -> R.string.invalid_character
    }

    this.text = resources.getString(messageId)
}