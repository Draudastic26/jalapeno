package com.draudastic.battlensake

import com.draudastic.models.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class Description(val name: String, val color: String, val head: String, val tail: String)

abstract class BattleSnake {

    abstract val description: Description
    protected abstract fun decideMove(moveRequest: MoveRequest): MoveResponse

    fun describe(): DescribeResponse {
        logger.info { "[${description.name}] Describe!" }
        return DescribeResponse("1", "Draudastic", description.color, description.head, description.tail, "1")
    }

    fun start(startRequest: StartRequest) {
        logger.info { "[${description.name}] Start! ${startRequest.game.id}" }
    }

    fun move(moveRequest: MoveRequest): MoveResponse {
        logger.info { "[${description.name}] Move!" }
        return decideMove(moveRequest)
    }

    fun end(endRequest: EndRequest) {
        logger.info { "[${description.name}] End! ${endRequest.game.id}" }
    }
}
