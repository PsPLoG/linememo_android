package com.psplog.linememo.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.loader.content.CursorLoader
import java.io.File
import java.util.*


class PhotoUtils {

    companion object {

        private fun getPath(context: Context, uri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursorLoader = CursorLoader(context, uri, projection, null, null, null)
            val cursor = cursorLoader.loadInBackground() ?: return "null"
            val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(index)
        }

        fun isNotHttpString(link: String) = !link.toLowerCase().contains("http")

        fun copyImageUriToFile(context: Context, srcUri: Uri, destFile: File) {
            val srcFile = File(getPath(context, srcUri))

            srcFile.inputStream().use { fis ->
                destFile.outputStream().use { fos ->
                    fis.copyTo(fos)
                }
            }
        }

        fun createUUID(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }

        open class DeletableImageItem(
            val uri: Any,
            val listener: OnDeletableImageClick,
            var visibleDeleteButton: Boolean = true
        ) {
            interface OnDeletableImageClick {
                fun onDeletableImageClick(fileName: String)
            }

            fun getUriInFileName(): String {
                var link = uri.toString()
                if (isNotHttpString(link)) {
                    link = uri.toString().split("/").last()
                }
                return link
            }
        }
    }
}