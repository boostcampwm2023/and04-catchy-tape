package com.ohdodok.catchytape.feature.playlist

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText

class NewPlaylistDialog : DialogFragment() {

    interface NewPlaylistDialogListener {
        fun onPositiveButtonClicked(dialog: DialogFragment, title: String)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_new_playlist, null)

            val positiveButton: Button = view.findViewById(R.id.btn_save)
            val negativeButton: Button = view.findViewById(R.id.btn_cancel)
            val playlistTitleEditText: TextInputEditText = view.findViewById(R.id.et_playlist_title)

            val newPlaylistDialogListener = parentFragment as? NewPlaylistDialogListener
            positiveButton.setOnClickListener {
                newPlaylistDialogListener?.onPositiveButtonClicked(this, playlistTitleEditText.text.toString())
                dialog?.cancel()
            }

            negativeButton.setOnClickListener {
                dialog?.cancel()
            }

            builder.setView(view).create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "NewPlaylistDialog"
    }

}