package com.psplog.linememo.adapter

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

class MemoListAdapter(var context: Context, var list: MutableList<Memo>) :
    RecyclerView.Adapter<MemoListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_memo_list, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(holder) {
            memoTitle.text = list[position].memoTitle
            memoContent.text = list[position].memoContent
            Glide.with(context)
                .load(list[position].thumbnail)
                .into(memoThumbnail)
            itemView.setOnClickListener { startMemoContentActivity(position) }
            memoCheckbox.isChecked=false
            memoCheckbox.setOnCheckedChangeListener { compoundButton, b ->
                list[position].isSelected = b
            }
        }
    }

    private fun startMemoContentActivity(position: Int) {
        val intent = Intent(context, AddEditMemoActivity::class.java)
        intent.putExtra("memo_id", list[position].memoId)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoTitle: TextView = itemView.findViewById(R.id.tv_memo_list_title)
        val memoContent: TextView = itemView.findViewById(R.id.tv_memo_list_content)
        val memoThumbnail: ImageView = itemView.findViewById(R.id.iv_memo_list_thumbnail)
        val memoCheckbox: CheckBox = itemView.findViewById(R.id.cb_memo_list_delete)
    }
}