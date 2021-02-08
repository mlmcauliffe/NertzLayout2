package com.example.nertzlayout2

import android.animation.ValueAnimator

interface AnimationOp {
    val totalDistance: Float
    fun progress(fraction: Float)
}

class CardAnimator(val op: AnimationOp) {

    companion object {
        val msPerCardWidth = 20
    }

    val animator = ValueAnimator.ofFloat(0f, 1f).also {
        it.setDuration((msPerCardWidth * op.totalDistance).toLong())
        it.addUpdateListener(object: ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(va: ValueAnimator) {
                op.progress(va.animatedFraction)
            }
        })
    }

    fun start() {
        animator.start()
    }

    fun stop() {
        animator.cancel()
    }

    fun finish() {
        animator.end()
    }
}