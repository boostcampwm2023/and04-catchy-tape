package com.ohdodok.catchytape.feature.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.login.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(BuildConfig.GOOGLE_SERVER_ID)
        .build()

    private val googleLoginLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result  ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.login(task.result.idToken, task.result.email)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setUpGoogleLoginBtn()
    }

    private fun setUpGoogleLoginBtn(){
        binding.btnGoogleLogin.setOnClickListener {
            val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            googleLoginLauncher.launch(mGoogleSignInClient.signInIntent)
        }
    }

    private fun observeEvent(){
        repeatOnStarted {
            viewModel.events.collect { event ->
                when(event){
                    is LoginEvent.NavigateToHome -> {

                    }
                    is LoginEvent.NavigateToNickName -> {
                        val action = LoginFragmentDirections.actionLoginFragmentToNicknameFragment()
                    }
                }
            }
        }
    }
}
