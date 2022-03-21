package com.draudastic.battlensake

import com.draudastic.models.*
import com.draudastic.utils.distance
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val utils = SnakeUtils(moveRequest)

        val avoidPositions = utils.getStaticAvoidPositions()

        val possibleMoves = getPossibleMoves(moveRequest.you.head, avoidPositions)

        val closestFood = utils.getClosestFood()

        val nextMove = if (closestFood != null) {
            goToFood(moveRequest.you.head, closestFood, possibleMoves)
        } else {
            possibleMoves.first()
        }

        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }

    private fun goToFood(head: Position, closestFood: Food, possibleMoves: Collection<Move>): Move {
        return possibleMoves.minByOrNull { distance(getMovePosition(head, it), closestFood.position) }
            ?: possibleMoves.random()
    }
}
