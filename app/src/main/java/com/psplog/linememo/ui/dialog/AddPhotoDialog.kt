package com.psplog.linememo.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.psplog.linememo.R


class AddPhotoDialog : DialogFragment() {
    lateinit var listener: AddPhotoDialogListener

    interface AddPhotoDialogListener {
        fun onDialogCameraClick()
        fun onDialogGalleryClick()
        fun onDialogLinkClick(link: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_addphoto, null)
            builder.setView(initView(view))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setOnClickListener(listener: AddPhotoDialogListener) {
        this.listener = listener
    }

    private fun initView(view: View): View {
        view.findViewById<ImageView>(R.id.btn_addphoto_add_gallery).setOnClickListener {
            listener.onDialogGalleryClick()
            dismiss()
        }
        view.findViewById<ImageView>(R.id.btn_addphoto_add_camera).setOnClickListener {
            listener.onDialogCameraClick()
            dismiss()
        }
        view.findViewById<ImageView>(R.id.iv_addphoto_add_link).setOnClickListener {
            if (View.GONE == view.findViewById<LinearLayout>(R.id.ll_addphoto_input).visibility) {
                view.findViewById<LinearLayout>(R.id.ll_addphoto_input).visibility = View.VISIBLE
            }

            view.findViewById<Button>(R.id.btn_addphoto_add).setOnClickListener {
                val strUri =
                    view.findViewById<EditText>(R.id.et_addphoto_link).text.toString().trim()
                listener.onDialogLinkClick(strUri)
                dismiss()
            }
        }
        return view
    }
}