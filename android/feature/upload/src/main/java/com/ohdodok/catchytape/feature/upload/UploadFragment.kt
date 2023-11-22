package com.ohdodok.catchytape.feature.upload

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.viewModels
import com.ohdodok.catchytape.catchytape.upload.R
import com.ohdodok.catchytape.catchytape.upload.databinding.FragmentUploadBinding
import com.ohdodok.catchytape.core.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UploadFragment : BaseFragment<FragmentUploadBinding>(R.layout.fragment_upload) {
    private val viewModel: UploadViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri == null) return@registerForActivityResult
        uri.path?.let { viewModel.uploadImage(File(it)) }
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            binding.btnFile.text = getFileName(uri)
            uri.path?.let { viewModel.uploadAudio(File(it)) }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setUpFileBtn()
        setupBackStack(binding.tbUpload)
        setupSelectThumbnailImage()
    }

    private fun setUpFileBtn() {
        binding.btnFile.setOnClickListener {
            filePickerLauncher.launch("audio/*")
        }
    }

    private fun setupSelectThumbnailImage() {
        binding.cvUploadThumbnail.setOnClickListener {
            imagePickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }

    private fun getFileName(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }
        return null
    }
}