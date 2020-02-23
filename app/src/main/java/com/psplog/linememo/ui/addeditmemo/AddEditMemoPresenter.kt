package com.psplog.linememo.ui.addeditmemo

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

class AddEditMemoPresenter(
    val context: AppCompatActivity,
    var memoId: Int,
    private val addEditView: AddEditMemoContract.View
) : AddEditMemoContract.Presenter {
    private val autoClearedDisposable = AutoClearedDisposable(context, true)
    private val memoDAO by lazy { MemoDataBase.provideMemoDAO(context) }
    private val memoImageDAO by lazy { MemoDataBase.provideMemoImageDAO(context) }
    private val memoImageQueue = ArrayList<String>()

    override fun start() {
        context.lifecycle.addObserver(autoClearedDisposable)
        autoClearedDisposable.addAll(
            getMemoContents(),
            getMemoImageContents()
        )
    }

    override fun addMemo(memo: Memo) {
        autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
            memoId = memoDAO.addMemo(Memo(memo.memoTitle, memo.memoContent, memo.thumbnail, memoId))
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
            memoImageQueue.clear()
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

    override fun deleteMemo(memo: Memo) {
        autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
            memoDAO.deleteMemo(Memo(memo.memoTitle, memo.memoContent, memo.thumbnail, memoId))
            deleteMemoImage(memo.memoId)
        })
    }

    private fun deleteMemoImage(memoId: Int): Disposable =
        memoImageDAO.getMemoImage(memoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::deleteMemoImageFile, this::logLocalizedMessage)

    private fun deleteMemoImageFile(item: List<MemoImage>) {
        for (item in item) {
            if (PhotoUtils.isHttpString(item.memoUri))
                continue

            val deleteFile = File(context.filesDir, item.memoUri)
            if (isNotDelete(deleteFile)) {
                logMessage("fileNotFindException:${deleteFile}")
            } else {
                autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
                    memoImageDAO.deleteMemoImage(item.memoUri)
                })
            }
        }
    }

    override fun deleteMemoImageInQueue(fileName: String) {
        val deleteFile = File(context.filesDir, fileName)
        if (!PhotoUtils.isHttpString(fileName)) {
            deleteFile.delete()
        }
        memoImageQueue.remove(fileName)
        autoClearedDisposable.add(RxJavaScheduler.runOnIoScheduler {
            memoImageDAO.deleteMemoImage(fileName)
        })
    }

    private fun isNotDelete(deleteFile: File) = !deleteFile.delete()

    private fun logLocalizedMessage(it: Throwable) {
        Log.d(TAG_ADD_EDIT_PRESENTER, it.localizedMessage)
    }

    private fun logMessage(msg: String) {
        Log.d(TAG_ADD_EDIT_PRESENTER, msg)
    }

    companion object {
        private const val TAG_ADD_EDIT_PRESENTER = "ADD_EDIT_PR"
    }
}