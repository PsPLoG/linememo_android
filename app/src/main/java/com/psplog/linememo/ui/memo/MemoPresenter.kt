package com.psplog.linememo.ui.memo

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.psplog.linememo.R
import com.psplog.linememo.database.MemoDataBase
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.database.local.MemoImage
import com.psplog.linememo.utils.AutoClearedDisposable
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

    override fun deleteMemoImage(memoId: Int): Disposable =
        memoImageDAO.getMemoImage(memoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::deleteMemoImageFile, this::throwLog)

    private fun deleteMemoImageFile(item: List<MemoImage>) {
        for (image in item) {
            val deleteFile = File(context.filesDir, image.memoUri)
            if (isNotDelete(deleteFile)) {
                toastMessage(R.string.memo_toast_file_delete_fail)
            }
        }
    }

    private fun isNotDelete(deleteFile: File) = !deleteFile.delete()

    private fun toastMessage(resId: Int) {
        Toast.makeText(
            context,
            context.resources.getText(resId),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun throwLog(it: Throwable) {
        Log.d(TAG_MEMO_PRESENTER, it.localizedMessage)
    }

    companion object {
        private const val TAG_MEMO_PRESENTER = "MEMO_PR"
    }
}