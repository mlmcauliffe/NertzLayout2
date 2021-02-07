package com.example.nertzlayout2

import android.animation.ValueAnimator

class MoveAnimator(val stage: StagedMove) {

    val animator = ValueAnimator.ofFloat(0f, 1f)

    init {
        animator.setDuration(50)
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
        animator.end()
    }
}