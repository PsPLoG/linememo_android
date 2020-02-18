package com.psplog.linememo.ui.dialog

import android.app.Dialog
import android.content.Context
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
        fun onDialogCameraClick(dialog: DialogFragment)
        fun onDialogGalleryClick(dialog: DialogFragment)
        fun onDialogLinkClick(dialog: DialogFragment, link: String)
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

    private fun initView(view: View) : View {
        view.findViewById<ImageView>(R.id.btn_addphoto_add_gallery).setOnClickListener {
            listener.onDialogGalleryClick(this)
            dismiss()
        }
        view.findViewById<ImageView>(R.id.btn_addphoto_add_camera).setOnClickListener {
            listener.onDialogCameraClick(this)
            dismiss()
        }
        view.findViewById<ImageView>(R.id.iv_addphoto_add_link).setOnClickListener {
            if(view.findViewById<LinearLayout>(R.id.ll_addphoto_input).visibility==View.GONE) {
                view.findViewById<LinearLayout>(R.id.ll_addphoto_input).visibility = View.VISIBLE
            }

            val strUri = view.findViewById<EditText>(R.id.et_addphoto_link).text.toString()
            view.findViewById<Button>(R.id.btn_addphoto_add).setOnClickListener {
                listener.onDialogLinkClick(this,strUri)
                dismiss()
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AddPhotoDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement AddPhotoDialogListener")
            )
        }
    }

}