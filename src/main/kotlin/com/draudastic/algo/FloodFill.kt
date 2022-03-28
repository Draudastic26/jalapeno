package com.draudastic.algo

import com.draudastic.battlensake.BoardState
import com.draudastic.battlensake.getMovePosition
import com.draudastic.models.Move
import com.draudastic.models.Position
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


data class FloodFillSet(val x1: Int, val x2: Int, val y: Int, val dy: Int)
enum class FloodFillAlgo { SimpleFill, AdvancedFill }

object FloodFill {

    fun BoardState.getFillCount(
        possibleMoves: Collection<Move>,
        algo: FloodFillAlgo = FloodFillAlgo.SimpleFill
    ): Map<Move, Int> {
        return possibleMoves.associateWith {
            val pos = this.you.head.getMovePosition(it)
            val fillCount = when (algo) {
                FloodFillAlgo.SimpleFill -> this.simpleFill(pos.x, pos.y)
                FloodFillAlgo.AdvancedFill -> this.advancedFill(pos.x, pos.y)
            }
            logger.info { "[$algo] $it fill count: $fillCount" }
            fillCount
        }
    }

    fun BoardState.removeClosedAreas(
        possibleMoves: Collection<Move>,
        algo: FloodFillAlgo = FloodFillAlgo.SimpleFill
    ): Collection<Move> {
        return possibleMoves.filter {
            val pos = this.you.head.getMovePosition(it)
            val fillCount = when (algo) {
                FloodFillAlgo.SimpleFill -> this.simpleFill(pos.x, pos.y)
                FloodFillAlgo.AdvancedFill -> this.advancedFill(pos.x, pos.y)
            }
            val include = fillCount > this.you.length
            if (!include) {
                logger.info { "[FloodFill] Removing $it - fill count: $fillCount <= length: ${this.you.length}" }
            }
            include
        }
    }

    // Returns the number of "filled" pixels
    private fun BoardState.simpleFill(x: Int, y: Int): Int {
        var count = 0
        val stack = mutableSetOf(Position(x, y))
        val filledPixel = this.avoidPositions.toMutableSet()
        while (stack.isNotEmpty() && count <= this.you.length) {
            val pos = stack.last().also { stack.remove(it) }
            if (pos !in filledPixel) {
                count += 1
                filledPixel.add(pos)
                stack.add(Position(pos.x, pos.y + 1))
                stack.add(Position(pos.x, pos.y - 1))
                stack.add(Position(pos.x + 1, pos.y))
                stack.add(Position(pos.x - 1, pos.y))
            }
        }
        return count
    }

    private val setList = mutableSetOf<Position>()

    // WIP: somehow inf loop :D
    private fun BoardState.advancedFill(startX: Int, startY: Int): Int {
        setList.clear()

        if (!this.inside(startX, startY)) return 0

        val s = mutableSetOf<FloodFillSet>()
        s.add(FloodFillSet(startX, startX, startY, 1))
        s.add(FloodFillSet(startX, startX, startY - 1, -1))

        while (s.isNotEmpty()) {
            val cur = s.random().also { s.remove(it) }
            var x1 = cur.x1
            val x2 = cur.x2
            val y = cur.y
            val dy = cur.dy

            var x = cur.x1
            if (this.inside(x, y)) {
                while (this.inside(x - 1, y)) {
                    set(x - 1, y)
                    x -= 1
                }
            }
            if (x < x1) {
                s.add(FloodFillSet(x, x1 - 1, y - dy, dy * -1))
            }
            while (x1 <= x2) {
                while (this.inside(x1, y)) {
                    set(x1, y)
                    x1 += 1
                }
                s.add(FloodFillSet(x, x1 - 1, y + dy, dy))
                if (x1 - 1 > x2) {
                    s.add(FloodFillSet(x2 + 1, x1 - 1, y - dy, dy * -1))
                }
                x1 += 1
                while (x1 < x2 && !this.inside(x1, y)) x1 += 1
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
