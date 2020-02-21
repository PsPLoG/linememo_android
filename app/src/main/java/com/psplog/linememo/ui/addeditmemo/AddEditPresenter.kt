package com.psplog.linememo.ui.addeditmemo

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.psplog.linememo.database.MemoDataBase
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.database.local.MemoImage
import com.psplog.linememo.utils.AutoActivatedDisposable
import com.psplog.linememo.utils.AutoClearedDisposable
import com.psplog.linememo.utils.RxJavaScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddEditPresenter(
    val context: AppCompatActivity,
    var memoId: Int,
    val addEditView: AddEditContract.View
) : AddEditContract.Presenter {

    private val disposables = AutoClearedDisposable(context)
    private val memoDAO by lazy { MemoDataBase.provideMemoDAO(context) }
    private val memoImageDAO by lazy { MemoDataBase.provideMemoImageDAO(context) }
    private val memoImageQueue = ArrayList<MemoImage>()

    override fun start() {
        disposables.cleanUp()
        context.lifecycle.addObserver(disposables)
        context.lifecycle.addObserver(AutoActivatedDisposable(context) {
            loadMemo()
            getMemoImage()

        })
    }

    override fun addPhoto(uri: String) {
        memoImageQueue += MemoImage(uri, memoId)
    }

    override fun clearPhotoQueqe() {
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoImageDAO.addMemoImage(memoImageQueue)
            memoImageQueue.clear()
        })
    }


    override fun addMemo(memo: Memo) {
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoId = memoDAO.addMemo(memo).toInt()
            clearPhotoQueqe()
        })

    }

    override fun loadMemo(): Disposable =
        memoDAO.getMemoContent(memoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ item ->
                addEditView.showMemoContent(item)
                Log.d("ADD_EDIT", "loadMemo:" + item.memoId)
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

    override fun deleteMemo(memo: Memo) {
        disposables.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.deleteMemo(memo)
        })
    }


}