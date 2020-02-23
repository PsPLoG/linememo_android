package com.psplog.linememo.utils

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoClearedDisposable(
    private val lifecycleOwner: AppCompatActivity,
    private val alwaysClearOnStop: Boolean = true,
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
) : LifecycleObserver {

    fun add(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        compositeDisposable.add(disposable)
    }

    fun addAll(vararg disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        compositeDisposable.addAll(*disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun cleanUp() {
        Log.d("autoclear", "ONSTOP")
        if (!alwaysClearOnStop && !lifecycleOwner.isFinishing) {
            return
        }
        compositeDisposable.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        Log.d("autoclear", "ONDESTROY")
        compositeDisposable.clear()
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}