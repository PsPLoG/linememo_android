package com.psplog.linememo.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.psplog.linememo.R

class SavingDialog : DialogFragment() {
    lateinit var listener: SavingDialogListener

    interface SavingDialogListener {
        fun onDialogSaveClick()
        fun onDialogExit()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.saving)
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogSaveClick()
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
                .setNeutralButton(R.string.exit,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogExit()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setOnClickListener(listener: SavingDialogListener) {
        this.listener = listener
    }
}