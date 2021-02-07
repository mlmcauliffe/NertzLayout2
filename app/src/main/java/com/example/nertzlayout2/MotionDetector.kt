package com.example.nertzlayout2

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

abstract class FullOnGestureListener: GestureDetector.SimpleOnGestureListener() {
    abstract fun onScrollEnd()
}

class FullOnGestureListenerAdapter(val child: FullOnGestureListener): FullOnGestureListener() {
    private var prevX = 0f
    private var prevY = 0f

    override fun onDown(e: MotionEvent): Boolean {
        prevX = e.rawX
        prevY = e.rawY
        return child.onDown(e)
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return child.onScroll(e1, e2, e2.rawX - prevX, e2.rawY - prevY).also {
            prevX = e2.rawX
            prevY = e2.rawY
        }
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?,
        velocityX: Float, velocityY: Float): Boolean {
        return child.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onScrollEnd() {
        child.onScrollEnd()
    }
}

class FullGestureDetector(context: Context, val listener: FullOnGestureListener)
    : GestureDetector(context, listener), View.OnTouchListener {

    init {
        setIsLongpressEnabled(false)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return onTouchEvent(event).also {
            if (event.action == MotionEvent.ACTION_UP) {
                view.performClick()
                listener.onScrollEnd()
            }
        }
    }
}
