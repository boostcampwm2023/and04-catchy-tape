package com.ohdodok.catchytape.feature.upload

import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import com.ohdodok.catchytape.catchytape.upload.R
import com.ohdodok.catchytape.catchytape.upload.databinding.FragmentUploadBinding
import com.ohdodok.catchytape.core.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadFragment : BaseFragment<FragmentUploadBinding>(R.layout.fragment_upload) {
    private val viewModel: UploadViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri == null) return@registerForActivityResult

        viewModel.uploadImage(uri.toFile())
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult

            viewModel.uploadAudio(uri.toFile())
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        setupBackStack(binding.tbUpload)
        setupSelectThumbnailImage()
    }

    private fun setupSelectThumbnailImage() {
        binding.cvUploadThumbnail.setOnClickListener {
            imagePickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }
}