package com.psplog.linememo.ui.memo

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.psplog.linememo.database.MemoDataBase
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.database.local.MemoImage
import com.psplog.linememo.utils.AutoClearedDisposable
import com.psplog.linememo.utils.PhotoUtils
import com.psplog.linememo.utils.RxJavaScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class MemoPresenter(
    val context: AppCompatActivity,
    private val memoView: MemoContract.View
) : MemoContract.Presenter {

    private val memoDAO by lazy { MemoDataBase.provideMemoDAO(context) }
    private val memoImageDAO by lazy { MemoDataBase.provideMemoImageDAO(context) }
    private val autoClearedDisposable = AutoClearedDisposable(context)
    override fun start() {
        context.lifecycle.addObserver(autoClearedDisposable)
        autoClearedDisposable.add(getMemoList())
    }

    override fun getMemoList(): Disposable {
        return memoDAO.getMemo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(memoView::showMemoList, this::throwLog)
    }

    override fun deleteMemo(memo: Memo) {
        autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.deleteMemo(memo)
            deleteMemoImage(memo.memoId)
        })
    }

    private fun deleteMemoImage(memoId: Int): Disposable =
        memoImageDAO.getMemoImage(memoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::deleteMemoImageFile, this::throwLog)

    private fun deleteMemoImageFile(item: List<MemoImage>) {
        for (item in item) {
            if (PhotoUtils.isHttpString(item.memoUri))
                continue

            val deleteFile = File(context.filesDir, item.memoUri)
            if (isNotDelete(deleteFile)) {
                logMessage("fileNotFindException:${deleteFile}")
            }
            autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
                memoImageDAO.deleteMemoImage(item.memoUri)
            })
        }
    }

    private fun isNotDelete(deleteFile: File) = !deleteFile.delete()

    private fun throwLog(it: Throwable) {
        Log.d(TAG_MEMO_PRESENTER, it.localizedMessage)
    }

    private fun logMessage(msg: String) {
        Log.d(TAG_MEMO_PRESENTER, msg)
    }

    companion object {
        private const val TAG_MEMO_PRESENTER = "MEMO_PR"
    }
}