package com.ohdodok.catchytape.feature.login

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.feature.login.databinding.FragmentNicknameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NicknameFragment : BaseFragment<FragmentNicknameBinding>(R.layout.fragment_nickname) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAppbar()
    }

    private fun setUpAppbar() {
        binding.tbNickname.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}