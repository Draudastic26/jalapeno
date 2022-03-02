package com.draudastic.battlensake

import com.draudastic.models.*
import com.draudastic.utils.distance
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class Jalapeno : BattleSnake() {

    override val info = Info("Jalapeño", "#004d00", "pixel", "pixel", "1.0.0")

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val avoidPositions = mutableSetOf<Position>().toHashSet()
        // avoid walls
        avoidPositions += wallPositions(moveRequest.board.width, moveRequest.board.height)
        // avoid hazards
        avoidPositions += moveRequest.board.hazards.map { it.position }
        // avoid snakes
        avoidPositions += moveRequest.board.snakes.flatMap { snake -> snake.body.map { body -> body.position } }

        logger.info { "[${info.name}] Head at ${moveRequest.you.head}" }
        logger.info { "[${info.name}] Avoid $avoidPositions" }

        val possibleMoves = getPossibleMoves(moveRequest.you.head, avoidPositions)
        val closestFood = getClosedFood(moveRequest.you.head, moveRequest.board.food)

        val nextMove = if (closestFood != null) {
            logger.info { "[${info.name}] Food at ${closestFood.position}" }
            goToFood(moveRequest.you.head, closestFood, possibleMoves)
        } else {
            possibleMoves.first()
        }

        logger.info { "[${info.name}] Chose $nextMove out of $possibleMoves" }
        return MoveResponse(nextMove)
    }

    private fun goToFood(head: Position, closestFood: Food, possibleMoves: Collection<Move>): Move {
        return possibleMoves.minByOrNull { distance(head, closestFood.position) } ?: possibleMoves.first()
    }

    private fun getClosedFood(head: Position, foods: Collection<Food>): Food? {
        return foods.minByOrNull { distance(head, it.position) }
    }

    private fun getPossibleMoves(head: Position, avoidPositions: HashSet<Position>): Collection<Move> {
        val possibleMoves = mutableListOf(Move.Up, Move.Right, Move.Down, Move.Left)
        Move.values().forEach { move ->
            val nextPos = nextPosition(head, move)
            logger.info { "[${info.name}] Next $nextPos" }
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
        for (x in 0 until width) walls += Position(x, -1)
        for (y in 0 until height) walls += Position(-1, y)
        return walls
    }
}
