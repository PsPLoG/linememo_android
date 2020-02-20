package com.psplog.linememo.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.psplog.linememo.R
import java.io.File
import java.util.*


class PhotoUtils {

    companion object {
        private fun getPath(context: Context, uri: Uri): String { //39. 갤러리에서 인텐트로 받은 이미지의 주소(uri)는 한번에 안받아지므로 따로 정의해주는 매쏘드
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursorLoader = CursorLoader(context, uri, projection, null, null, null)
            val cursor = cursorLoader.loadInBackground() ?: return "null"
            val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(index)
        }

        fun copyImageUriToFile(context: Context, srcUri: Uri, destFile: File) {
            var srcFile = File(getPath(context, srcUri))

            srcFile.inputStream().use { fis ->
                destFile.outputStream().use { fos ->
                    fis.copyTo(fos)
                }
            }
        }

        fun addPhotoView(view: View, uri: Any) {

            var imageView = ImageView(view.context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            imageView.adjustViewBounds = true
            when (uri) {
                is Uri -> Glide.with(view.context).load(uri).error(R.drawable.load_fail_image).into(imageView)
                is String -> Glide.with(view.context).load(uri).error(R.drawable.load_fail_image).into(imageView)
                is File -> Glide.with(view.context).load(uri).error(R.drawable.load_fail_image).into(imageView)
            }
            view.findViewById<LinearLayout>(R.id.ll_content_image_list).addView(imageView)
        }

        fun createUUID(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }
    }
}