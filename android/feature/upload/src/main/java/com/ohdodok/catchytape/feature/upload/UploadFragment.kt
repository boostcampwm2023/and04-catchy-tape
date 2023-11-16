package com.ohdodok.catchytape.feature.upload

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ohdodok.catchytape.catchytape.upload.R
import com.ohdodok.catchytape.catchytape.upload.databinding.FragmentUploadBinding
import com.ohdodok.catchytape.core.ui.BaseFragment

class UploadFragment : BaseFragment<FragmentUploadBinding>(R.layout.fragment_upload) {
    private val viewModel: UploadViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupBackStack(binding.tbUploadAppbar)
    }
}