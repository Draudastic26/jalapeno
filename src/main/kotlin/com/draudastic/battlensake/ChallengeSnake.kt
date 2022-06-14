package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.Position
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class ChallengeSnake(override val info: Info) : BattleSnake() {
    override fun decideMove(moveRequest: MoveRequest): Move {
        val possibleMoves = action.getPossibleMoves(state.you.head, state.avoidPositions)

        val target = Position(0, 0)

        val nextMove = action.moveTowards(state.you.head, target, possibleMoves)

        logger.info { "[${info.name}] Go $nextMove!" }
        return nextMove
    }
}
