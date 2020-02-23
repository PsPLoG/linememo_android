package com.psplog.linememo.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.psplog.linememo.R

class DeleteDialog : DialogFragment() {
    lateinit var listener: DeleteDialogListener

    interface DeleteDialogListener {
        fun onDialogDeleteClick()
        fun onDialogCancelClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.delete_tooltip))
                .setPositiveButton(R.string.menu_addedit_delete,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogDeleteClick()
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogCancelClick()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setOnClickListener(listener: DeleteDialogListener) {
        this.listener = listener
    }
}