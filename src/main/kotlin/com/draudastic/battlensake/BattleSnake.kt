package com.draudastic.battlensake

import com.draudastic.models.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class Appearance(val name: String, val color: String, val head: String, val tail: String)

abstract class BattleSnake {

    abstract val appearance: Appearance
    protected abstract fun decideMove(moveRequest: MoveRequest): MoveResponse

    fun describe(): DescribeResponse {
        logger.info { "[${appearance.name}] Describe!" }
        return DescribeResponse("1", "Draudastic", appearance.color, appearance.head, appearance.tail, "1")
    }

    fun start(startRequest: StartRequest) {
        logger.info { "[${appearance.name}] Start! ${startRequest.game.id}" }
    }

    fun move(moveRequest: MoveRequest): MoveResponse {
        logger.info { "[${appearance.name}] Move!" }
        return decideMove(moveRequest)
    }

    fun end(endRequest: EndRequest) {
        logger.info { "[${appearance.name}] End! ${endRequest.game.id}" }
    }
}
