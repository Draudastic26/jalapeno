package com.draudastic.utils

import com.draudastic.models.Position
import kotlin.math.pow

fun distance(from: Position, to: Position): Float {
    return ((to.x - from.x).toFloat().pow(2) + (to.y - from.y).toFloat().pow(2)).pow(0.5f)
}