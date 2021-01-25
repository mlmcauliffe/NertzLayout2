package com.example.nertzlayout2

import android.animation.ValueAnimator

typealias ValueAnimatorAdapterOnUpdateListener = (Float) -> Unit
typealias ValueAnimatorAdapterOnDoneListener = () -> Unit

class ValueAnimatorAdapter(
    val onUpdate: ValueAnimatorAdapterOnUpdateListener,
    val onDone: ValueAnimatorAdapterOnDoneListener)
    : ValueAnimator.AnimatorUpdateListener {

    override fun onAnimationUpdate(p0: ValueAnimator) {
        val f = p0.animatedFraction
        onUpdate(f)
        if (f == 1f) {
            onDone()
        }
    }
}