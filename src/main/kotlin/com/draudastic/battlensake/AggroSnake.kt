package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class AggroSnake(override val info: Info) : BattleSnake() {

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        // Avoid static fields
        val utils = SnakeUtils(moveRequest)
        val avoidPositions = utils.getStaticAvoidPositions(includeHazards = false)
        val possibleMoves = getPossibleMoves(utils.you.head, avoidPositions)

        val defaultTarget = utils.you.body.last().position
        var targetPosition = defaultTarget

        // Go to food if not the largest snake or health < 25
        if (!moveRequest.you.isLargestSnake(moveRequest.board.snakes) || utils.you.health < 25) {
            targetPosition = utils.getClosestFood()?.position ?: defaultTarget
            logger.info { "[${info.name}] Go to closest food at $targetPosition" }
        } else {
            // Try to attack others
            val closestSnake = utils.getClosestEnemy()
            closestSnake?.let { victim ->
                val possibleMovesByEnemy = getPossibleMoves(victim.head, avoidPositions)
                targetPosition = getMovePosition(victim.head, possibleMovesByEnemy.randomOrNull() ?: Move.Up)
            }
        }

        val nextMove = goToPosition(moveRequest.you.head, targetPosition, possibleMoves)
        logger.info { "[${info.name}] Go $nextMove!" }
        return MoveResponse(nextMove)
    }
}
