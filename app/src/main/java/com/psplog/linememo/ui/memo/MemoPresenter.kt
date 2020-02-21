package com.psplog.linememo.ui.memo

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.psplog.linememo.utils.AutoActivatedDisposable
import com.psplog.linememo.utils.AutoClearedDisposable
import com.psplog.linememo.database.MemoDataBase
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.utils.RxJavaScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MemoPresenter(val context: AppCompatActivity,
                    val memoView: MemoContract.View) : MemoContract.Presenter {

    private val memoDAO by lazy { MemoDataBase.provideMemoDAO(context) }

    private val disposables = AutoClearedDisposable(context)
    override fun start() {
        context.lifecycle.addObserver(disposables)
        context.lifecycle.addObserver(AutoActivatedDisposable(context) {
            getMemoList()
        })
    }

    override fun getMemoList(): Disposable =
            memoDAO.getMemo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ item ->
                        memoView.showMemoList(item)
                    }) {
                        Log.d("Main", it.localizedMessage)
                    }
    override fun deleteMemo(memo: Memo) {
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.deleteMemo(memo)
        })
    }
}