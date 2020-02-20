package com.psplog.linememo.ui.addeditmemo

import com.psplog.linememo.BasePresenter
import com.psplog.linememo.BaseView
import com.psplog.linememo.utils.database.local.Memo
import com.psplog.linememo.utils.database.local.MemoImage
import io.reactivex.disposables.Disposable

interface AddEditContract {

    interface View : BaseView<Presenter> {
        fun showAddPhotoDialog()
        fun showMemoContent(memoContent: Memo)
        fun showMemoContentImage(memoContentImageList: List<MemoImage>)
    }

    interface Presenter : BasePresenter {
        fun addPhoto(uri: String)
        fun addMemo(memo: Memo)
        fun getMemo(): Disposable?
        fun getMemoImage(): Disposable?
        fun deleteMemoImage()
        fun deleteMemo(memo: Memo)
    }
}