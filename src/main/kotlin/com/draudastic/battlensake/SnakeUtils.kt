package com.draudastic.battlensake

import com.draudastic.models.Food
import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.Position
import com.draudastic.utils.distance

class SnakeUtils(private val moveRequest: MoveRequest) {

    val width = moveRequest.board.width
    val height = moveRequest.board.height
    private val foods = moveRequest.board.food
    private val head = moveRequest.you.head

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

    fun getStaticAvoidPositions(includeHazards: Boolean = true): HashSet<Position> {
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

fun getPossibleMoves(head: Position, avoidPositions: HashSet<Position>): Collection<Move> {
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
