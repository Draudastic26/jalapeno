package com.draudastic.battlensake

import com.draudastic.algo.FloodFill.removeClosedAreas
import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class AggroSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): Move {
        // Avoid static fields
        val avoidPositions = state.avoidPositions
        var possibleMoves = action.getPossibleMoves(state.you.head, avoidPositions)

        logger.info { "[${info.name}] Remaining moves: $possibleMoves" }
        val possibleMovesAvoidLargerSnakes =
            possibleMoves.filter { !state.largerSnakeNearby(state.you.head.getMovePosition(it)) }
        if (possibleMovesAvoidLargerSnakes.isNotEmpty()) possibleMoves = possibleMovesAvoidLargerSnakes

        val possibleMovesWithoutClosedAreaMove = state.removeClosedAreas(possibleMoves)
        if (possibleMovesWithoutClosedAreaMove.isNotEmpty()) possibleMoves = possibleMovesWithoutClosedAreaMove

        val defaultTarget = state.you.body.last().position
        var targetPosition = defaultTarget

        // Go to food if not the largest snake or health < 25
        if (!moveRequest.you.isLargestSnake(moveRequest.board.snakes) || state.you.health < 25) {
            targetPosition = state.getClosestFood()?.position ?: defaultTarget
            logger.info { "[${info.name}] Go to closest food at $targetPosition" }
        } else {
            // Try to attack others
            val closestSnake = state.getClosestEnemy()
            closestSnake?.let { victim ->
                val possibleMovesByEnemy = action.getPossibleMoves(victim.head, avoidPositions)
                targetPosition = victim.head.getMovePosition(possibleMovesByEnemy.randomOrNull() ?: Move.Up)
                logger.info { "[${info.name}] Attack ${victim.name}" }
            }
        }

        val nextMove = action.moveTowards(moveRequest.you.head, targetPosition, possibleMoves)
        logger.info { "[${info.name}] Go $nextMove!" }
        return nextMove
    }
}
