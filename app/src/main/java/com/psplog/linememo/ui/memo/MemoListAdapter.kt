package com.psplog.linememo.ui.memo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.psplog.linememo.R
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.ui.addeditmemo.AddEditMemoActivity
import com.psplog.linememo.utils.PhotoUtils
import java.io.File

class MemoListAdapter(var context: Context, val list: MutableList<Memo>) :
    RecyclerView.Adapter<MemoListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_memo_list, parent, false)
        with(Holder(view)) {
            itemView.setOnClickListener {
                startMemoContentActivity(adapterPosition)
            }
            memoCheckbox.setOnCheckedChangeListener { _, isChecked ->
                list[adapterPosition].isSelected = isChecked
            }
            return this
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(holder) {
            when {
                isNoneTitleNoneContent(position) -> // Image Case
                    memoTitle.text = context.resources.getText(R.string.memo_nocontent)
                isNoneTitle(position) -> // None Title case
                    memoTitle.text = list[position].memoContent
                else -> { // Normal case
                    memoTitle.text = list[position].memoTitle
                    memoContent.text = list[position].memoContent
                }
            }
            memoThumbnail.setImageResource(0)
            if (isThumbnailNullOrEmpty(position)) {
                with(list[position].thumbnail) {
                    if (PhotoUtils.isNotHttpString(this!!)) {
                        val imageTemp = File(context.filesDir, this)
                        Glide.with(context)
                            .load(imageTemp)
                            .centerCrop()
                            .into(memoThumbnail)
                    } else {
                        Glide.with(context)
                            .load(this)
                            .centerCrop()
                            .into(memoThumbnail)
                    }
                }
            }
            memoCheckbox.isChecked = false
        }
    }

    fun setItems(list: List<Memo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    private fun startMemoContentActivity(position: Int) {
        val intent = Intent(context, AddEditMemoActivity::class.java)
        intent.putExtra("memo_id", list[position].memoId)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun isNoneTitleNoneContent(position: Int) =
        isNoneTitle(position) && list[position].memoContent.isEmpty()

    private fun isNoneTitle(position: Int) = list[position].memoTitle.isEmpty()

    private fun isThumbnailNullOrEmpty(position: Int) = !list[position].thumbnail.isNullOrEmpty()

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoTitle: TextView = itemView.findViewById(R.id.tv_memo_list_title)
        val memoContent: TextView = itemView.findViewById(R.id.tv_memo_list_content)
        val memoThumbnail: ImageView = itemView.findViewById(R.id.iv_memo_list_thumbnail)
        val memoCheckbox: CheckBox = itemView.findViewById(R.id.cb_memo_list_delete)


    }
}