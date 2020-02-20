package com.psplog.linememo.ui.addeditmemo

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.psplog.linememo.utils.AutoActivatedDisposable
import com.psplog.linememo.utils.AutoClearedDisposable
import com.psplog.linememo.utils.RxJavaScheduler
import com.psplog.linememo.utils.database.MemoDataBase
import com.psplog.linememo.utils.database.local.Memo
import com.psplog.linememo.utils.database.local.MemoImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddEditPresenter(val context: AppCompatActivity,
                       val memoId: Int,
                       val addEditView: AddEditContract.View
) : AddEditContract.Presenter {

    private val disposables = AutoClearedDisposable(context)
    private val memoDAO by lazy { MemoDataBase.provideMemoDAO(context) }
    private val memoImageDAO by lazy { MemoDataBase.provideMemoImageDAO(context) }

    override fun start() {
        context.lifecycle.addObserver(disposables)
        context.lifecycle.addObserver(AutoActivatedDisposable(context) {
            getMemo()
            getMemoImage()

        })
    }

    override fun addPhoto(uri: String) {
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoImageDAO.addMemoImage(MemoImage(uri, memoId))
        })
    }

    override fun addMemo(memo: Memo) {
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.addMemo(memo)
        })
    }

    override fun getMemo(): Disposable =
            memoDAO.getMemoContent(memoId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ item ->
                        addEditView.showMemoContent(item)
                        Log.d("ADD_EDIT", "getMemo:" + item.memoId)
                    }) {
                        Log.d("ADD_EDIT", it.localizedMessage)
                    }


    override fun getMemoImage(): Disposable =
            memoImageDAO.getMemoImage(memoId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ item ->
                        addEditView.showMemoContentImage(item)
                        Log.d("ADD_EDIT", "getMemoImage:" + item.size)
                    }) {
                        Log.d("ADD_EDIT", it.localizedMessage)
                    }


    override fun deleteMemoImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteMemo(memo : Memo) {
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.deleteMemo(memo)
        })
    }


}