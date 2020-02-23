package com.psplog.linememo.ui.addeditmemo

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.psplog.linememo.database.MemoDataBase
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.database.local.MemoImage
import com.psplog.linememo.utils.AutoClearedDisposable
import com.psplog.linememo.utils.RxJavaScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddEditMemoPresenter(
    val context: AppCompatActivity,
    var memoId: Int,
    val addEditView: AddEditMemoContract.View
) : AddEditMemoContract.Presenter {
    private val autoClearedDisposable = AutoClearedDisposable(context)
    private val memoDAO by lazy { MemoDataBase.provideMemoDAO(context) }
    private val memoImageDAO by lazy { MemoDataBase.provideMemoImageDAO(context) }
    private val memoImageQueue = ArrayList<String>()

    override fun start() {
        context.lifecycle.addObserver(autoClearedDisposable)
        autoClearedDisposable.addAll(getMemoContents(),
            getMemoImageContents())
    }

    override fun addMemo(memo: Memo) {
        autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
            memoId = memoDAO.addMemo(memo)
                .toInt()
            pushImageQueue()
        })
    }

    override fun addMemoImageInQueue(uri: String) {
        memoImageQueue += uri
    }

    override fun pushImageQueue() {
        autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
            val temp = ArrayList<MemoImage>()
            memoImageQueue.map { item -> MemoImage(item, memoId) }
                .toCollection(temp)
            memoImageDAO.addMemoImage(temp)
        })
    }

    private fun getMemoContents(): Disposable {
        return memoDAO.getMemoContent(memoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(addEditView::showMemoContent, this::logLocalizedMessage)
    }

    private fun getMemoImageContents(): Disposable {
        return memoImageDAO.getMemoImage(memoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(addEditView::showMemoContentImage, this::logLocalizedMessage)
    }

    private fun logLocalizedMessage(it: Throwable) {
        Log.d(TAG_ADD_EDIT_PRESENTER, it.localizedMessage)
    }

    override fun deleteMemo(memo: Memo) {
        autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.deleteMemo(memo)
            memoImageDAO.deleteMemoImage(memo.memoId)
        })
    }

    override fun deleteMemoImageInQueue(fileName: String) {
        if (!memoImageQueue.remove(fileName)) {
            autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
                Log.d(TAG_ADD_EDIT_PRESENTER, "파일삭제$fileName")
                memoImageDAO.deleteMemoImage(fileName)
            })
        }
    }

    companion object {
        private const val TAG_ADD_EDIT_PRESENTER = "ADD_EDIT_PR"
    }
}