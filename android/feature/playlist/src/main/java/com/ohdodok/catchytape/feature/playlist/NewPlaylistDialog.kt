package com.ohdodok.catchytape.feature.playlist

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment

class NewPlaylistDialog(private val onPositiveButtonClicked: View.OnClickListener) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_new_playlist, null)

            val positiveButton: Button = view.findViewById(R.id.btn_save)
            val negativeButton: Button = view.findViewById(R.id.btn_cancel)

            positiveButton.setOnClickListener {
                onPositiveButtonClicked.onClick(it)
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
}