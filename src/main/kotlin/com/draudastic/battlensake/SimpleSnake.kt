package com.draudastic.battlensake

import com.draudastic.algo.FloodFill.removeClosedAreas
import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): Move {
        val avoidPositions = state.avoidPositions
        var possibleMoves = action.getPossibleMoves(state.you.head, avoidPositions)

        logger.info { "[${info.name}] Remaining moves: $possibleMoves" }

        val possibleMovesAvoidLargerSnakes =
            possibleMoves.filter { !state.largerSnakeNearby(state.you.head.getMovePosition(it)) }
        if (possibleMovesAvoidLargerSnakes.isNotEmpty()) possibleMoves = possibleMovesAvoidLargerSnakes

        val possibleMovesWithoutClosedAreaMove = state.removeClosedAreas(possibleMoves)
        if (possibleMovesWithoutClosedAreaMove.isNotEmpty()) possibleMoves = possibleMovesWithoutClosedAreaMove

        val target = state.getClosestFood()?.position ?: state.you.body.last().position

        val nextMove = action.moveTowards(state.you.head, target, possibleMoves)

        logger.info { "[${info.name}] Go $nextMove!" }
        return nextMove
    }
}
