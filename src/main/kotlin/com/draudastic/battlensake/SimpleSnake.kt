package com.draudastic.battlensake

import com.draudastic.algo.FloodFill.removeClosedAreas
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import com.draudastic.models.Position
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class SimpleSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val avoidPositions = state.avoidPositions
        var possibleMoves = action.getPossibleMoves(state.you.head, avoidPositions)

        logger.info { "[${info.name}] Remaining moves: $possibleMoves" }
        possibleMoves = possibleMoves.filter { !largerSnakeNearby(state.you.head.getMovePosition(it)) }
        val possibleMovesWithoutClosedAreaMove = state.removeClosedAreas(possibleMoves)
        if (possibleMovesWithoutClosedAreaMove.isNotEmpty()) possibleMoves = possibleMovesWithoutClosedAreaMove

        val target = state.getClosestFood()?.position ?: state.you.body.last().position

        val nextMove = action.moveTowards(state.you.head, target, possibleMoves)

        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }

    private fun largerSnakeNearby(pos: Position): Boolean {
        val adjacentPositions = pos.getAllMovePositions()
        val otherBigHeads = state.otherSnakes.filter { it.length >= state.you.length }.map { it.body.first().position }
        return adjacentPositions.any { it in otherBigHeads }
    }
}
