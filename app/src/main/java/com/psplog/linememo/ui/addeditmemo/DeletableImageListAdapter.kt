package com.psplog.linememo.ui.addeditmemo

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.psplog.linememo.R
import com.psplog.linememo.utils.PhotoUtils
import java.io.File

class DeletableImageListAdapter(
    var context: Context,
    val list: MutableList<PhotoUtils.Companion.DeletableImageItem>
) :
    RecyclerView.Adapter<DeletableImageListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.imageview_deletable, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(holder) {
            val errorListener = object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(context, "잘못된 URL 입니다.", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
//                    (context as Activity).runOnUiThread {
//                        setDeleteButtonVisible(false)
//                    }
                    return false
                }
            }

            when (list[position].uri) {
                is Uri -> Glide.with(context).load(list[position].uri).error(R.drawable.load_fail_image).addListener(
                    errorListener
                ).into(deletableImageVIew)
                is String -> Glide.with(context).load(list[position].uri).error(R.drawable.load_fail_image).addListener(
                    errorListener
                ).into(deletableImageVIew)
                is File -> Glide.with(context).load(list[position].uri).error(R.drawable.load_fail_image).addListener(
                    errorListener
                ).into(deletableImageVIew)
            }

            if (list[position].visibleDeleteButton) {
                deletableImageViewDelete.visibility = View.VISIBLE
            } else {
                deletableImageViewDelete.visibility = View.GONE
            }

            deletableImageViewDelete.setOnClickListener {
                list[position].listener.onDeletableImageClick(list[position].getUriInFileName())
                list.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    fun setDeleteButtonVisible(isVisible: Boolean) {
        for (item in list) {
            item.visibleDeleteButton = isVisible
        }
        notifyDataSetChanged()
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deletableImageVIew: ImageView = itemView.findViewById(R.id.iv_deletable_imageview)
        val deletableImageViewDelete: ImageView = itemView.findViewById(R.id.iv_deletable_delete)
    }
}