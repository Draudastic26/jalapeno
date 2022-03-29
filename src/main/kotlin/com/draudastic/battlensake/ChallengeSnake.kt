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

        val nextMove = if (state.you.health < 5) {
            val target = state.getClosestFood()?.position ?: state.you.body.last().position
            action.moveTowards(state.you.head, target, possibleMoves)
        } else {
            avoidFood(possibleMoves)
        }


        logger.info { "[${info.name}] Go $nextMove!" }
        return nextMove
    }

    private fun avoidFood(possibleMoves: Collection<Move>): Move {
        val newPossibleMoves = possibleMoves.toMutableSet()
        newPossibleMoves.forEach { move ->
            val nextPos = state.you.head.getMovePosition(move)
            if (state.board.food.map { it.position }.contains(nextPos)) newPossibleMoves.remove(move)
        }
        return newPossibleMoves.randomOrNull() ?: possibleMoves.randomOrNull() ?: Move.values().random()
    }
}
