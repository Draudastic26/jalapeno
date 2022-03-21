package com.draudastic.algo

import com.draudastic.battlensake.BoardState

data class SpanFillSet(val x1: Int, val x2: Int, val y: Int, val dy: Int)

object SpanFilling {
/*
 fn fill(x, y):
  if not Inside(x, y) then return
  let s = new empty queue or stack
  Add (x, x, y, 1) to s
  Add (x, x, y - 1, -1) to s
  while s is not empty:
    Remove an (x1, x2, y, dy) from s
    let x = x1

    if Inside(x, y):
      while Inside(x - 1, y):
        Set(x - 1, y)
        x = x - 1
    if x < x1:
      Add (x, x1-1, y-dy, -dy) to s
    while x1 <= x2:
      while Inside(x1, y):
        Set(x1, y)
        x1 = x1 + 1
      Add (x, x1 - 1, y+dy, dy) to s
      if x1 - 1 > x2:
        Add (x2 + 1, x1 - 1, y-dy, -dy) to s
      x1 = x1 + 1
      while x1 < x2 and not Inside(x1, y):
        x1 = x1 + 1
      x = x1
*/


    // Returns the number of "filled" pixels
    fun BoardState.fill(x: Int, y: Int): Int {
        val filledPixel = 0

        if (!this.inside(x, y)) return 0

        val s = mutableListOf<SpanFillSet>()
        s.add(SpanFillSet(x, x, y, 1))
        s.add(SpanFillSet(x, x, y - 1, -1))

        while (s.isNotEmpty()) {
            val curr = s.random()
            val x = curr.x1
            if(this.inside(x, y)) {
                while(this.inside(x - 1, y)) {
                    this.set(x -1, y)
                    x = x -1
                }
            }
        }

        return filledPixel
    }

    private fun BoardState.inside(x: Int, y: Int): Boolean {

    }

    private fun BoardState.set(x: Int, y: Int) {

    }
}
