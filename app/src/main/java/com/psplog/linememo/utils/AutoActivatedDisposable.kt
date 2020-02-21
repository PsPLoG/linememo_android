package com.psplog.linememo.utils

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

class AutoActivatedDisposable(private val lifecycleOwner: LifecycleOwner,
                              private val func: () -> Disposable) : LifecycleObserver {

    private var disposable: Disposable? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activate() {
        Log.d("autoact","ONSTART")
        disposable = func.invoke()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun deactivate() {
        Log.d("autoact","ONSTOP")
        disposable?.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        Log.d("autoact","ONDESTRY")
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}