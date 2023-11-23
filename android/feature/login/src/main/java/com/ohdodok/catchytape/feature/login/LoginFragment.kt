package com.ohdodok.catchytape.feature.login

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.login.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by activityViewModels()

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
        .requestEmail()
        .build()

    private val googleLoginLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                task.result.idToken?.let { token -> viewModel.login(token) }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setUpGoogleLoginBtn()
        observeEvents()
    }

    private fun setUpGoogleLoginBtn() {
        binding.btnGoogleLogin.setOnClickListener {
            val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            googleLoginLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is LoginEvent.NavigateToHome -> {
                        val intent = Intent()
                        intent.component = ComponentName("com.ohdodok.catchytape", "com.ohdodok.catchytape.MainActivity")
                        startActivity(intent)
                        activity?.finish()
                    }

                    is LoginEvent.NavigateToNickName -> {
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToNicknameFragment(
                                googleToken = event.googleToken
                            )
                        )
                    }

                    is LoginEvent.ShowMessage -> {
                        val messageId = fetchCtErrorMessageId(event.error)
                        showMessage(messageId)
                    }
                }
            }
        }
    }


}
