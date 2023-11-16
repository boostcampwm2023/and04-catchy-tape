package com.ohdodok.catchytape.feature.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.login.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        // TODO : Nickname Fragment Test 를 위한 코드로 추후 변경 해야함
        binding.btnGoogleLogin.setOnClickListener {
            findNavController().navigate(R.id.action_login_fragment_to_nickname_fragment)
        }
    }
}