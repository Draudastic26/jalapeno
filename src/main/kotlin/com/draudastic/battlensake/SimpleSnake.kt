package com.draudastic.battlensake

import com.draudastic.algo.SpanFilling.fill
import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val avoidPositions = state.avoidPositions
        var possibleMoves = action.getPossibleMoves(state.you.head, avoidPositions)

        possibleMoves = removeClosedAreas(possibleMoves)

        val closestFood = state.getClosestFood()
        val nextMove = if (closestFood != null) {
            action.moveTowards(state.you.head, closestFood.position, possibleMoves)
        } else {
            possibleMoves.random()
        }



        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }

    private fun removeClosedAreas(possibleMoves: Collection<Move>): Collection<Move> {
        return possibleMoves.filter {
            val pos = state.you.head.getMovePosition(it)
            state.fill(pos.x, pos.y) > state.you.length
        }
    }
}
