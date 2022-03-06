package com.draudastic.battlensake

import com.draudastic.models.*
import com.draudastic.utils.distance
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val isWrapped = moveRequest.game.ruleset.name == "wrapped"

        val avoidPositions = mutableSetOf<Position>().toHashSet()
        // avoid walls if not wrapped
        if (!isWrapped)
            avoidPositions += wallPositions(moveRequest.board.width, moveRequest.board.height)
        // avoid hazards if health
        val hazards = moveRequest.board.hazards.map { it.position }
        avoidPositions += hazards
        // avoid snakes
        val snakes = moveRequest.board.snakes.flatMap { snake -> snake.body.map { body -> body.position } }
        avoidPositions += snakes
        // Add wrapped positions to avoid
        if (isWrapped) {
            avoidPositions += hazards.flatMap { getMirroredPositions(it, moveRequest.board.width, moveRequest.board.height) }
            avoidPositions += snakes.flatMap { getMirroredPositions(it, moveRequest.board.width, moveRequest.board.height) }
        }

        val possibleMoves = getPossibleMoves(moveRequest.you.head, avoidPositions)
        val closestFood = getClosedFood(
            moveRequest.you.head,
            moveRequest.board.food,
            isWrapped,
            moveRequest.board.width,
            moveRequest.board.height
        )

        val nextMove = if (closestFood != null) {
            goToFood(moveRequest.you.head, closestFood, possibleMoves)
        } else {
            possibleMoves.first()
        }

        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }

    private fun goToFood(head: Position, closestFood: Food, possibleMoves: Collection<Move>): Move {
        return possibleMoves.minByOrNull { distance(nextPosition(head, it), closestFood.position) }
            ?: possibleMoves.first()
    }

    private fun getClosedFood(
        head: Position,
        foods: Collection<Food>,
        isWrapped: Boolean,
        boardWidth: Int,
        boardHeight: Int
    ): Food? {
        // Add "wrapped" foods when in wrapped mode
        return if (isWrapped) {
            val withWrappedFoods = foods.toMutableSet()
            foods.forEach { food ->
                withWrappedFoods += getMirroredPositions(food.position, boardWidth, boardHeight).map {
                    Food(it.x, it.y)
                }
            }
            withWrappedFoods.minByOrNull { distance(head, it.position) }
        } else {
            foods.minByOrNull { distance(head, it.position) }
        }
    }

    private fun getMirroredPositions(position: Position, boardWidth: Int, boardHeight: Int): Collection<Position> {
        return listOf(
            Position(position.x, position.y + boardHeight),
            Position(position.x + boardWidth, position.y),
            Position(position.x, position.y - boardHeight),
            Position(position.x - boardWidth, position.y)
        )
    }

    private fun getPossibleMoves(head: Position, avoidPositions: HashSet<Position>): Collection<Move> {
        val possibleMoves = mutableListOf(Move.Up, Move.Right, Move.Down, Move.Left)
        Move.values().forEach { move ->
            val nextPos = nextPosition(head, move)
            if (avoidPositions.contains(nextPos)) possibleMoves.remove(move)
        }
        return possibleMoves
    }

    private fun nextPosition(start: Position, direction: Move): Position {
        return when (direction) {
            Move.Up -> Position(start.x, start.y + 1)
            Move.Right -> Position(start.x + 1, start.y)
            Move.Down -> Position(start.x, start.y - 1)
            Move.Left -> Position(start.x - 1, start.y)
        }
    }

    private fun wallPositions(width: Int, height: Int): Collection<Position> {
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
}
