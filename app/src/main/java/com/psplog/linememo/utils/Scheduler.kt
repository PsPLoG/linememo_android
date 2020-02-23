package com.psplog.linememo.utils

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