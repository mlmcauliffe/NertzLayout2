package com.example.nertzlayout2

import android.view.View
import android.view.ViewGroup

class CascadePileLayout(parent: ViewGroup, color: Int,
    x: Int, y: Int, width: Int, height: Int, override val cardOffset: Int):
    PileLayout(parent, color, x, y, width, height)