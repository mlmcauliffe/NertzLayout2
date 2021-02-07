package com.example.nertzlayout2

interface Undo {
    fun apply(): MoveAnimator?
}