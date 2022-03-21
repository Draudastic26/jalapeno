package com.draudastic.battlensake

import com.draudastic.algo.SpanFilling.fill
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val avoidPositions = state.avoidPositions
        val possibleMoves = action.getPossibleMoves(state.you.head, avoidPositions)

        val closestFood = state.getClosestFood()
        val nextMove = if (closestFood != null) {
            action.moveTowards(state.you.head, closestFood.position, possibleMoves)
        } else {
            possibleMoves.random()
        }
        state.fill()

        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }

}
