package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val utils = SnakeUtils(moveRequest)

        val avoidPositions = utils.getStaticAvoidPositions()

        val possibleMoves = getPossibleMoves(utils.you.head, avoidPositions)

        val closestFood = utils.getClosestFood()
        val nextMove = if (closestFood != null) {
            goToPosition(utils.you.head, closestFood.position, possibleMoves)
        } else {
            possibleMoves.random()
        }

        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }
}
