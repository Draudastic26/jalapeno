package com.draudastic.battlensake

import com.draudastic.algo.FloodFill.removeClosedAreas
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val avoidPositions = state.avoidPositions
        var possibleMoves = action.getPossibleMoves(state.you.head, avoidPositions)

        logger.info { "[${info.name}] Remaining moves: $possibleMoves" }
        possibleMoves = state.removeClosedAreas(possibleMoves)

        val closestFood = state.getClosestFood()
        var target = state.you.body.last().position

        if (closestFood != null) {
            target = closestFood.position
        }

        val nextMove = action.moveTowards(state.you.head, target, possibleMoves)

        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }

}
