package com.psplog.linememo.ui.memo

import com.psplog.linememo.BasePresenter
import com.psplog.linememo.BaseView
import com.psplog.linememo.utils.database.local.Memo
import io.reactivex.disposables.Disposable

interface MemoContract {

    interface View : BaseView<Presenter> {
        fun showMemoList(item: List<Memo>)
    }

    interface Presenter : BasePresenter {
        fun getMemoList(): Disposable
    }
}