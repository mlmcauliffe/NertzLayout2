package com.example.nertzlayout2

import android.animation.ValueAnimator

class MoveAnimator(val stage: StagedMove) {

    companion object {
        val duration = 50L
    }

    val animator = ValueAnimator.ofFloat(0f, 1f)

    init {
        animator.setDuration(duration)
        animator.addUpdateListener(
                ValueAnimatorAdapter({ fraction ->
                    stage.pile.animateMove(stage.startingAt,
                            (stage.distanceX * fraction).toInt(),
                            (stage.distanceY * fraction).toInt())
                }, {
                    stage.pile.reposition(stage.startingAt)
                })
        )
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