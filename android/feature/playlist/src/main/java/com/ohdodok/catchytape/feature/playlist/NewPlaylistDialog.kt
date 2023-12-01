package com.ohdodok.catchytape.feature.playlist

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.ohdodok.catchytape.feature.playlist.databinding.DialogNewPlaylistBinding

class NewPlaylistDialog : DialogFragment() {

    interface NewPlaylistDialogListener {
        fun onPositiveButtonClicked(dialog: DialogFragment, title: String)
    }

    private var _binding: DialogNewPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            _binding = DialogNewPlaylistBinding.inflate(layoutInflater)

            val newPlaylistDialogListener = parentFragment as? NewPlaylistDialogListener
            binding.btnSave.setOnClickListener {
                newPlaylistDialogListener?.onPositiveButtonClicked(this, binding.etPlaylistTitle.text.toString())
                dialog?.cancel()
            }

            binding.btnCancel.setOnClickListener {
                dialog?.cancel()
            }

            AlertDialog.Builder(activity).setView(binding.root).create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NewPlaylistDialog"
    }
}