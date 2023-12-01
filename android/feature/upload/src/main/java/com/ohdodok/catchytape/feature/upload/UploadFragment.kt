package com.ohdodok.catchytape.feature.upload

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.ohdodok.catchytape.catchytape.upload.R
import com.ohdodok.catchytape.catchytape.upload.databinding.FragmentUploadBinding
import com.ohdodok.catchytape.core.ui.BaseFragment
import com.ohdodok.catchytape.core.ui.toMessageId
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class UploadFragment : BaseFragment<FragmentUploadBinding>(R.layout.fragment_upload) {
    private val viewModel: UploadViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri == null) return@registerForActivityResult
        uri.toPath()?.let { path -> viewModel.uploadImage(File(path)) }
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            uri.toPath()?.let { path -> viewModel.uploadAudio(File(path)) }
            binding.btnFile.text = getFileName(uri)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        observeEvents()

        setUpFileBtn()
        setUpCompleteBtn()
        setupSelectThumbnailImage()
        setupBackStack(binding.tbUpload)
    }

    private fun observeEvents() {
        repeatOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is UploadEvent.NavigateToBack -> {
                        findNavController().popBackStack()
                    }

                    is UploadEvent.ShowMessage -> {
                        showMessage(event.error.toMessageId())
                    }
                }
            }
        }
    }

    private fun setUpFileBtn() {
        binding.btnFile.setOnClickListener {
            filePickerLauncher.launch("audio/*")
        }
    }

    private fun setUpCompleteBtn() {
        binding.btnComplete.setOnClickListener {
            viewModel.uploadMusic()
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

    private fun Uri.toPath(): String? {
        val file = getFileName(this)?.let { File(requireContext().filesDir, it) }
        val inputStream = requireContext().contentResolver.openInputStream(this)
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }
        return file?.absolutePath
    }
}

@BindingAdapter("musicTitleIsValid")
fun TextInputLayout.bindMusicTitleValidation(state: MusicTitleState) {
    error = if (state.isValid || state.title.isEmpty()) null
    else resources.getString(R.string.invalid_music_title)
}
