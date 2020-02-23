package com.psplog.linememo.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.psplog.linememo.R
import java.io.File
import java.util.*


class PhotoUtils {

    companion object {

        private var deletableImageViewList = ArrayList<DeletableImageView>()

        private fun getPath(context: Context, uri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursorLoader = CursorLoader(context, uri, projection, null, null, null)
            val cursor = cursorLoader.loadInBackground() ?: return "null"
            val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(index)
        }

        fun isHttpString(link: String) = link.toLowerCase().contains("http")

        fun copyImageUriToFile(context: Context, srcUri: Uri, destFile: File) {
            val srcFile = File(getPath(context, srcUri))

            srcFile.inputStream().use { fis ->
                destFile.outputStream().use { fos ->
                    fis.copyTo(fos)
                }
            }
        }

        fun setVisibilityDeleteButton(visibility: Int) {
            for (item in deletableImageViewList) {
                item.deleteButton.visibility = visibility
            }
        }

        fun addPhotoView(view: View, uri: Any, listener: DeletableImageView.OnDeletableImageClick) {
            val deletableImageView =
                DeletableImageView(view, uri.toString().split("/").last(), listener)
            deletableImageViewList.add(deletableImageView)

            val errorListener = object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(view.context, "잘못된 URL 입니다.", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }

            when (uri) {
                is Uri -> Glide.with(view.context).load(uri).error(R.drawable.load_fail_image).addListener(
                    errorListener
                ).into(deletableImageView.imageView)
                is String -> Glide.with(view.context).load(uri).error(R.drawable.load_fail_image).addListener(
                    errorListener
                ).into(deletableImageView.imageView)
                is File -> Glide.with(view.context).load(uri).error(R.drawable.load_fail_image).addListener(
                    errorListener
                ).into(deletableImageView.imageView)
            }
            view.findViewById<LinearLayout>(R.id.ll_content_image_list)
                .addView(deletableImageView.deleteView)
        }

        fun createUUID(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }


        class DeletableImageView(
            view: View,
            private val fileName: String,
            listener: OnDeletableImageClick
        ) {
            var deleteView = FrameLayout(view.context)
            var imageView = ImageView(view.context)
            var deleteButton = ImageView(view.context)

            interface OnDeletableImageClick {
                fun onDeletableImageClick(fileName: String)
            }

            init {
                deleteButton.tag = "deleteButtonView"
                deleteButton.setImageResource(R.drawable.twotone_clear_black_36)
                deleteButton.setOnClickListener {
                    listener.onDeletableImageClick(fileName)
                    deletableImageViewList.remove(this)
                    deleteView.removeAllViews()
                }

                deleteView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                imageView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                imageView.adjustViewBounds = true
                deleteView.addView(imageView)
                deleteView.addView(deleteButton)
            }
        }
    }
}