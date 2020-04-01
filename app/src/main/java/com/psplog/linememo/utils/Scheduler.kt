package com.psplog.linememo.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class RxJavaScheduler {
    companion object {
        fun runOnIoScheduler(func: () -> Unit): Disposable =
            Completable.fromCallable(func)
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}
class TestRelativeLayout : RelativeLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //모드를 출력해 보자.
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        printMode("LayoutTest",widthMode)
        printMode("LayoutTest",heightMode)
    }
    private fun printMode(tag: String, mode: Int) {
        when (mode) {
            View.MeasureSpec.AT_MOST -> Log.v("CustomView-MeasureSpec", "$tag AT_MOST")
            View.MeasureSpec.EXACTLY -> Log.v("CustomView-MeasureSpec", "$tag EXACTLY")
            View.MeasureSpec.UNSPECIFIED -> Log.v("CustomView-MeasureSpec", "$tag UNSPECIFIED")
        }
    }
}

class TestRecyclerView : RecyclerView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //모드를 출력해 보자.
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        printMode("LayoutTestRECYCLER",widthMode)
        printMode("LayoutTestRECYCLER",heightMode)
    }
    private fun printMode(tag: String, mode: Int) {
        when (mode) {
            View.MeasureSpec.AT_MOST -> Log.v("CustomView-MeasureSpec", "$tag AT_MOST")
            View.MeasureSpec.EXACTLY -> Log.v("CustomView-MeasureSpec", "$tag EXACTLY")
            View.MeasureSpec.UNSPECIFIED -> Log.v("CustomView-MeasureSpec", "$tag UNSPECIFIED")
        }
    }
}