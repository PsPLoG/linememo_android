package com.psplog.linememo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.psplog.linememo.R
import com.psplog.linememo.ui.main.ContentActivity
import com.psplog.linememo.utils.database.Memo

class MemoListAdapter(var context: Context, var list: List<Memo>) : RecyclerView.Adapter<MemoListAdapter.Holder>(),View.OnClickListener{
    override fun onClick(v: View?) {
        val intent = Intent(context, ContentActivity::class.java)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_memo_list, parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.memoTitle.text=list[position].memoTitle
        holder.memoContent.text=list[position].memoContent
        Glide.with(context).load(list[position].thumbnail).into(holder.memoThumbnail)

        holder.memoTitle.setOnClickListener(this)
        holder.memoContent.setOnClickListener(this)
        holder.memoThumbnail.setOnClickListener(this)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoTitle : TextView = itemView.findViewById(R.id.tv_memo_list_title)
        val memoContent : TextView = itemView.findViewById(R.id.tv_memo_list_content)
        val memoThumbnail : ImageView = itemView.findViewById(R.id.iv_memo_list_thumbnail)
    }
}