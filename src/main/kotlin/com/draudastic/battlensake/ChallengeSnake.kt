package com.draudastic.battlensake

import com.draudastic.algo.FloodFill.removeClosedAreas
import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class ChallengeSnake(override val info: Info) : BattleSnake() {
    override fun decideMove(moveRequest: MoveRequest): Move {
        var possibleMoves = action.getPossibleMoves(state.you.head, state.avoidPositions)

        val possibleMovesWithoutClosedAreaMove = state.removeClosedAreas(possibleMoves)
        if (possibleMovesWithoutClosedAreaMove.isNotEmpty()) possibleMoves = possibleMovesWithoutClosedAreaMove

        var target = state.you.body.last().position
        if (state.you.health < 10) target = state.getClosestFood()?.position ?: target

        val nextMove = action.moveTowards(state.you.head, target, possibleMoves)

        logger.info { "[${info.name}] Go $nextMove!" }
        return nextMove
    }
}
