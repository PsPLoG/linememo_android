package com.psplog.linememo.ui.memo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.psplog.linememo.R
import com.psplog.linememo.adapter.MemoListAdapter
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.ui.addeditmemo.AddEditMemoActivity
import com.psplog.linememo.utils.AutoActivatedDisposable
import com.psplog.linememo.utils.AutoClearedDisposable
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.memo_fragment_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_addedit_delete -> removeCheckedMemo()
        }
        return true
    }

    override fun onClick(v: View?) {
        val intent = Intent(applicationContext, AddEditMemoActivity::class.java)
        startActivity(intent)
    }

    override fun showMemoList(item: MutableList<Memo>) {
        with(rv_memo_list.adapter as MemoListAdapter) {
            list = item
            notifyDataSetChanged()
        }
    }

    private fun removeCheckedMemo() {
        with(rv_memo_list.adapter as MemoListAdapter) {
            list.filter { it.isSelected }
                .forEach{
                    presenter.deleteMemo(it)
                    list.remove(it)
                }
            notifyDataSetChanged()
        }
    }

}
