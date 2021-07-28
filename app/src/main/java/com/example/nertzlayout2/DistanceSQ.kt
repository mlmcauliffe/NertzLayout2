package com.example.nertzlayout2

fun distanceSQ(x1: Int, x2: Int, y1: Int, y2: Int): Int {
    val xDist = x1 - x2
    val yDist = y1 - y2
    return (xDist * xDist) + (yDist * yDist)
}