package com.psplog.linememo.utils

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RxJavaScheduler {
    companion object {
        fun runOnIoScheduler(func: () -> Unit): Disposable =
            Completable.fromCallable(func)
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}
