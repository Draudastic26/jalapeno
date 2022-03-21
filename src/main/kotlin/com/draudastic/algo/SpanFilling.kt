package com.draudastic.algo

import com.draudastic.battlensake.BoardState
import com.draudastic.models.Position

data class SpanFillSet(val x1: Int, val x2: Int, val y: Int, val dy: Int)

object SpanFilling {

    private val setList = mutableSetOf<Position>()

    // Returns the number of "filled" pixels
    fun BoardState.fill(startX: Int, startY: Int): Int {
        setList.clear()

        if (!this.inside(startX, startY)) return 0

        val s = mutableListOf<SpanFillSet>()
        s.add(SpanFillSet(startX, startX, startY, 1))
        s.add(SpanFillSet(startX, startX, startY - 1, -1))

        while (s.isNotEmpty()) {
            val cur = s.random()
            var x1 = cur.x1
            val x2 = cur.x2
            val y = cur.y
            val dy = cur.dy

            var x = cur.x1
            if (this.inside(x, y)) {
                while (this.inside(x - 1, y)) {
                    set(x - 1, y)
                    x = x - 1
                }
            }
            if (x < x1) {
                s.add(SpanFillSet(x, x1 - 1, y - dy, dy * -1))
            }
            while (x1 <= x2) {
                while (this.inside(x1, y)) {
                    set(x1, y)
                    x1 = x1 + 1
                }
                s.add(SpanFillSet(x, x1 - 1, y + dy, dy))
                if (x1 - 1 > x2) {
                    s.add(SpanFillSet(x2 + 1, x1 - 1, y - dy, dy * -1))
                }
                x1 = x1 * 1
                while(x1 < x2 && !this.inside(x1, y)) {
                    x1 = x1 + 1
                }
                x = x1
            }
        }

        return setList.count()
    }

    private fun BoardState.inside(x: Int, y: Int): Boolean {
        val pos = Position(x, y)
        return pos !in this.avoidPositions && pos !in setList
    }

    private fun set(x: Int, y: Int) {
        setList.add(Position(x, y))
    }
}
