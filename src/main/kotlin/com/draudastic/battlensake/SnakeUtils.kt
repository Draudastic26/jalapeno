package com.draudastic.battlensake

import com.draudastic.models.*
import com.draudastic.utils.distance

class SnakeUtils(private val moveRequest: MoveRequest) {

    val width = moveRequest.board.width
    val height = moveRequest.board.height
    private val foods = moveRequest.board.food
    private val head = moveRequest.you.head
    private val allSnakes = moveRequest.board.snakes
    val you = moveRequest.you
    val otherSnakes = moveRequest.board.snakes.filter { it.id != you.id }

    val isWrapped = moveRequest.game.ruleset.name == "wrapped"

    fun getWallPositions(): Collection<Position> {
        val walls = mutableListOf<Position>()
        for (x in 0 until width) {
            walls += Position(x, -1)
            walls += Position(x, height)
        }
        for (y in 0 until height) {
            walls += Position(-1, y)
            walls += Position(width, y)
        }
        return walls
    }

    fun getClosestFood(): Food? {
        return if (isWrapped) {
            // Add "wrapped" foods when in wrapped mode
            val withWrappedFoods = foods.toMutableSet()
            foods.forEach { food ->
                val wrapped = food.position.getWrappedPositions(width, height)
                withWrappedFoods += wrapped.map { Food(it.x, it.y) }
            }
            withWrappedFoods.minByOrNull { distance(head, it.position) }
        } else foods.minByOrNull { distance(head, it.position) }
    }

    fun getClosestEnemy(): Snake? {
        return otherSnakes.minByOrNull { distance(head, it.head) }
    }

    fun getStaticAvoidPositions(includeHazards: Boolean = true): MutableSet<Position> {
        val avoidPositions = mutableSetOf<Position>().toHashSet()
        // avoid walls if not wrapped
        if (!isWrapped)
            avoidPositions += getWallPositions()
        // avoid hazards
        if (includeHazards) {
            val hazards = moveRequest.board.hazards.map { it.position }
            avoidPositions += hazards
            if (isWrapped) {
                avoidPositions += hazards.flatMap {
                    it.getWrappedPositions(width, height)
                }
            }
        }
        // avoid snakes
        val snakes = moveRequest.board.snakes.flatMap { snake -> snake.body.map { body -> body.position } }
        avoidPositions += snakes
        // Add wrapped snake positions to avoid
        if (isWrapped) {
            avoidPositions += snakes.flatMap {
                it.getWrappedPositions(width, height)
            }
        }
        return avoidPositions
    }
}

fun getPossibleMoves(head: Position, avoidPositions: MutableSet<Position>): Collection<Move> {
    val possibleMoves = mutableListOf(Move.Up, Move.Right, Move.Down, Move.Left)
    Move.values().forEach { move ->
        val nextPos = getMovePosition(head, move)
        if (avoidPositions.contains(nextPos)) possibleMoves.remove(move)
    }
    return possibleMoves
}

fun Position.getWrappedPositions(width: Int, height: Int): Collection<Position> {
    return listOf(
        Position(this.x, this.y + height),
        Position(this.x + width, this.y),
        Position(this.x, this.y - height),
        Position(this.x - width, this.y)
    )
}

fun getMovePosition(start: Position, direction: Move): Position {
    return when (direction) {
        Move.Up -> Position(start.x, start.y + 1)
        Move.Right -> Position(start.x + 1, start.y)
        Move.Down -> Position(start.x, start.y - 1)
        Move.Left -> Position(start.x - 1, start.y)
    }
}

fun getAllMovePositions(start: Position): Collection<Position> {
    return Move.values().map { getMovePosition(start, it) }
}

fun goToPosition(head: Position, targetPosition: Position, possibleMoves: Collection<Move>): Move {
    return possibleMoves.minByOrNull { distance(getMovePosition(head, it), targetPosition) }
        ?: possibleMoves.randomOrNull() ?: Move.Up
}

fun Snake.isLargestSnake(snakes: Collection<Snake>): Boolean {
    val otherSnakesLargerOrSame = snakes.filter { it.id != this.id && it.length >= this.length }
    return otherSnakesLargerOrSame.isEmpty()
}
