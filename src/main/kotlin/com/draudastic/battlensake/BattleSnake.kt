package com.draudastic.battlensake

import com.draudastic.models.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class Info(val name: String, val color: String, val head: String, val tail: String, val version: String)

abstract class BattleSnake {

    abstract val info: Info
    protected abstract fun decideMove(moveRequest: MoveRequest): Move

    protected val state = BoardState()
    protected val action = SnakeActions()

    fun info(): InfoResponse {
        logger.info { "[${info.name}] Info!" }
        return InfoResponse("1", "Draudastic", info.color, info.head, info.tail, info.version)
    }

    fun start(startRequest: StartRequest) {
        logger.info { "[${info.name}] Start! ${startRequest.game}" }
    }

    fun move(moveRequest: MoveRequest): MoveResponse {
        state.update(moveRequest)
        return MoveResponse(decideMove(moveRequest))
    }

    fun end(endRequest: EndRequest) {
        logger.info { "[${info.name}] End!" }
    }
}
