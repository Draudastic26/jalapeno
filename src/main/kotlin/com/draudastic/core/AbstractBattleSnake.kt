@file:OptIn(ExperimentalTime::class)

package com.draudastic.core

import com.draudastic.models.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val logger = KotlinLogging.logger {}


abstract class AbstractBattleSnake<T : SnakeContext> {

    abstract fun snakeContext(): T

    abstract fun gameStrategy(): GameStrategy<T>

    internal val strategy by lazy { gameStrategy() }

    private val contextMap = ConcurrentHashMap<String, T>()

    internal suspend fun process(call: ApplicationCall): GameResponse =
        try {
            val uri = call.request.uri
            val (pair, duration) =
                measureTimedValue {
                    when (uri) {
                        "/new/" -> describe(call)
                        "/new/start" -> start(call)
                        "/new/move" -> move(call)
                        "/new/end" -> end(call)
                        else -> throw IllegalAccessError("Invalid call made to the snake: $uri [${call.request.origin.remoteHost}]")
                    }
                }

            val context = pair.first
            val gameResponse = pair.second

            strategy.afterTurnActions.forEach { it.invoke(context, call, gameResponse, duration) }
            gameResponse
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong with ${call.request.uri}" }
            throw e
        }

    private fun describe(call: ApplicationCall): Pair<T?, GameResponse> =
        null to (strategy.describeActions.map { it.invoke(call) }.lastOrNull() ?: InfoResponse())

    private suspend fun start(call: ApplicationCall): Pair<T?, GameResponse> =
        snakeContext()
            .let { context ->
                val startRequest = call.receive<StartRequest>()
                //logger.info { "Creating new snake context for ${startRequest.gameId}" }
                context.resetStartTime()
                context.assignIds(startRequest.game.id, startRequest.you.id)
                context.assignRequestResponse(call)
                contextMap[context.snakeId] = context
                strategy.startActions.map { it.invoke(context, startRequest) }
                context to StartResponse
            }

    private suspend fun move(call: ApplicationCall): Pair<T?, GameResponse> {
        val moveRequest = call.receive<MoveRequest>()
        val context = contextMap[moveRequest.you.id]
            ?: throw NoSuchElementException("Missing context for user id: ${moveRequest.you.id}")
        assert(context.snakeId == moveRequest.you.id)

        context.assignRequestResponse(call)

        val (response, duration) =
            measureTimedValue {
                strategy.moveActions
                    .map { it.invoke(context, moveRequest) }
                    .lastOrNull() ?: throw IllegalStateException("Missing move action")
            }

        context.apply {
            computeTime += duration
            moveCount++
        }

        return context to response
    }

    private suspend fun end(call: ApplicationCall): Pair<T?, GameResponse> {
        val endRequest = call.receive<EndRequest>()
        val context = contextMap.remove(endRequest.you.id)
            ?: throw NoSuchElementException("Missing context for user id: ${endRequest.you.id}")
        assert(context.snakeId == endRequest.you.id)
        context.assignRequestResponse(call)
        return context to (strategy.endActions.map { it.invoke(context, endRequest) }.lastOrNull() ?: EndResponse())
    }
}
