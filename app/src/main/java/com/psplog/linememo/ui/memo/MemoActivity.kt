package com.psplog.linememo.ui.memo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import com.psplog.linememo.R
import com.psplog.linememo.adapter.MemoListAdapter
import com.psplog.linememo.data.MemoItem
import com.psplog.linememo.ui.main.ContentActivity
import com.psplog.linememo.utils.RxJavaScheduler
import com.psplog.linememo.utils.database.Memo
import com.psplog.linememo.utils.database.MemoDataBase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.content_memo.*

class MemoActivity : AppCompatActivity(), View.OnClickListener {
    internal val disposables = CompositeDisposable()
    internal val memoDAO by lazy { MemoDataBase.provideMemoDAO(this) }
    //TODO : 롱클릭 삭제기능
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)
        setSupportActionBar(toolbar)


        fab.setOnClickListener(this)


        var list = demoList()
        rv_memo_list.adapter = MemoListAdapter(applicationContext, list)
        rv_memo_list.layoutManager = LinearLayoutManager(applicationContext)
        adaptList()
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.addMemo(Memo())
        })
    }

    override fun onClick(v: View?) {
        val intent = Intent(applicationContext, ContentActivity::class.java)
        startActivity(intent)
    }

    private fun adaptList() = memoDAO.getMemo()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ item ->
            with(rv_memo_list.adapter as MemoListAdapter) {
                list = item
                notifyDataSetChanged()
            }
        }) {
            Log.d("Main", it.localizedMessage)
        }

    private fun demoList(): ArrayList<Memo> {
        var list = ArrayList<Memo>()
        list.add(Memo())
        list.add(Memo())
        list.add(Memo())
        return list
    }

}
