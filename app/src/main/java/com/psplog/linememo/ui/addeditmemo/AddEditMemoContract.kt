package com.psplog.linememo.ui.addeditmemo

import com.psplog.linememo.BasePresenter
import com.psplog.linememo.BaseView
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.database.local.MemoImage

interface AddEditMemoContract {

    interface View : BaseView<Presenter> {

        fun showAddPhotoDialog()

        fun showMemoContent(memoContent: Memo)

        fun showMemoContentImage(memoContentImageList: List<MemoImage>)

    }

    interface Presenter : BasePresenter {

        fun addMemo(memo: Memo)

        fun addMemoImageInQueue(uri: String)

        fun addMemoImageInDeleteQueue(uri: String)

        fun deleteMemo(memo: Memo)

        fun deleteMemoImageInQueue()

        fun pushImageQueue()

    }
}