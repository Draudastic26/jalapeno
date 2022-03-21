package com.draudastic.battlensake

import com.draudastic.models.*
import com.draudastic.utils.distance

class BoardState {
    private lateinit var moveRequest: MoveRequest

    val isWrapped: Boolean
        get() = moveRequest.game.ruleset.name == "wrapped"

    val boardWidth: Int
        get() = moveRequest.board.width

    val boardHeight: Int
        get() = moveRequest.board.height

    val avoidPositions: Set<Position>
        get() = getAvoidPositions()

    val you: Snake
        get() = moveRequest.you

    val board: Board
        get() = moveRequest.board

    val otherSnakes: Collection<Snake>
        get() = moveRequest.board.snakes.filter { it.id != you.id }

    private fun wallPositions(): Set<Position> {
        val walls = mutableSetOf<Position>()
        for (x in 0 until boardWidth) {
            walls += Position(x, -1)
            walls += Position(x, boardHeight)
        }
        for (y in 0 until boardHeight) {
            walls += Position(-1, y)
            walls += Position(boardWidth, y)
        }
        return walls
    }

    private fun getAvoidPositions(): HashSet<Position> {
        val avoidPositions = mutableSetOf<Position>().toHashSet()
        // avoid walls if not wrapped
        if (!isWrapped)
            avoidPositions += wallPositions()
        // avoid snakes (exclude heads)
        val snakes = board.snakes.flatMap { snake -> snake.body.drop(1).map { body -> body.position } }.toMutableSet()
        // Add own head again
        snakes.add(you.head)
        avoidPositions += snakes
        // Add wrapped snake positions to avoid
        if (isWrapped) {
            avoidPositions += snakes.flatMap {
                it.getWrappedPositions(boardWidth, boardHeight)
            }
        }
        return avoidPositions
    }

    fun update(newMoveRequest: MoveRequest) {
        moveRequest = newMoveRequest
    }

    fun getClosestFood(): Food? {
        return if (isWrapped) {
            // Add "wrapped" foods when in wrapped mode
            val withWrappedFoods = board.food.toMutableSet()
            board.food.forEach { food ->
                val wrapped = food.position.getWrappedPositions(boardWidth, boardHeight)
                withWrappedFoods += wrapped.map { Food(it.x, it.y) }
            }
            withWrappedFoods.minByOrNull { distance(you.head, it.position) }
        } else board.food.minByOrNull { distance(you.head, it.position) }
    }

    fun getClosestEnemy(): Snake? {
        return otherSnakes.minByOrNull { distance(you.head, it.head) }
    }
}
