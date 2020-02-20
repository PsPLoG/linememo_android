package com.psplog.linememo.ui.memo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.psplog.linememo.R
import com.psplog.linememo.adapter.MemoListAdapter
import com.psplog.linememo.ui.addeditmemo.AddEditMemoActivity
import com.psplog.linememo.utils.AutoActivatedDisposable
import com.psplog.linememo.utils.AutoClearedDisposable
import com.psplog.linememo.utils.database.MemoDataBase
import com.psplog.linememo.utils.database.local.Memo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.content_memo.*

class MemoActivity : AppCompatActivity(), View.OnClickListener, MemoContract.View {
    override lateinit var presenter: MemoContract.Presenter
    private val disposables = AutoClearedDisposable(this)

    //TODO : 롱클릭 삭제기능
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)
        setSupportActionBar(toolbar)

        fab.setOnClickListener(this)

        var list = ArrayList<Memo>()
        rv_memo_list.adapter = MemoListAdapter(applicationContext, list)
        rv_memo_list.layoutManager = LinearLayoutManager(applicationContext)

        presenter = MemoPresenter(this, this)

        lifecycle.addObserver(disposables)
        lifecycle.addObserver(AutoActivatedDisposable(this) {
            presenter.getMemoList()
        })
    }

    override fun onClick(v: View?) {
        val intent = Intent(applicationContext, AddEditMemoActivity::class.java)
        startActivity(intent)
    }

    override fun showMemoList(item: List<Memo>) {
        with(rv_memo_list.adapter as MemoListAdapter) {
            list = item
            notifyDataSetChanged()
        }
    }

}
