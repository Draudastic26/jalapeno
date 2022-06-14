
@file:Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction")

package com.draudastic.core

import com.draudastic.core.GameStrategy.Companion.afterTurnMsg
import com.draudastic.core.GameStrategy.Companion.describeMsg
import com.draudastic.core.GameStrategy.Companion.endMsg
import com.draudastic.core.GameStrategy.Companion.startMsg
import com.draudastic.models.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import mu.KLogging
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

fun <T : SnakeContext> strategy(verbose: Boolean = false, init: GameStrategy<T>.() -> Unit) =
  GameStrategy<T>()
    .apply {
      onDescribe { call ->
        logger.info { describeMsg(call) }
        InfoResponse()
      }

      onStart { context, request ->
        logger.info { startMsg(context, request) }
      }

      onEnd { context, request ->
        logger.info { endMsg(context, request) }
        EndResponse()
      }

      if (verbose) {
        onAfterTurn { context: T?, call, gameResponse, millis ->
          logger.info { afterTurnMsg(context, call, gameResponse, millis) }
        }
      }

      init.invoke(this)
    }

open class GameStrategy<T : SnakeContext> : KLogging() {

  internal val describeActions = mutableListOf<(call: ApplicationCall) -> InfoResponse>()
  internal val startActions = mutableListOf<(context: T, request: StartRequest) -> Unit>()
  internal val moveActions = mutableListOf<(context: T, request: MoveRequest) -> MoveResponse>()
  internal val endActions = mutableListOf<(context: T, request: EndRequest) -> EndResponse>()
  internal val afterTurnActions = mutableListOf<(
    context: T?,
    call: ApplicationCall,
    gameResponse: GameResponse,
    duration: Duration
  ) -> Unit>()

  fun onDescribe(block: (call: ApplicationCall) -> InfoResponse) = let { describeActions += block }

  fun onStart(block: (context: T, request: StartRequest) -> Unit) = let { startActions += block }

  fun onMove(block: (context: T, request: MoveRequest) -> MoveResponse) = let { moveActions += block }

  fun onEnd(block: (context: T, request: EndRequest) -> EndResponse) = let { endActions += block }

  fun onAfterTurn(
    block: (context: T?, call: ApplicationCall, gameResponse: GameResponse, duration: Duration) -> Unit
  ) =
    let { afterTurnActions += block }

  companion object {
    internal fun describeMsg(call: ApplicationCall) = "Describe from ${call.request.origin.host}"

    internal fun <T : SnakeContext> startMsg(context: T, request: StartRequest) =
      "Starting Game/Snake '${request.game.id}/${context.snakeId}' [${context.call.request.origin.host}]"

    internal fun <T : SnakeContext> endMsg(context: T, request: EndRequest): String =
      context.let {
        val avg =
          if (it.moveCount > 0)
            "\nAvg time/move: ${(it.computeTime.toDouble(DurationUnit.MILLISECONDS) / it.moveCount.toDouble()).milliseconds} "
          else
            ""

        "\nEnding Game/Snake '${request.game.id}/${it.snakeId}'" +
            "\nTotal moves: ${it.moveCount} " +
            "\nTotal game time: ${it.elapsedGameTime} " +
            "\nTotal compute time: ${it.computeTime}" +
            "$avg[${it.call.request.origin.host}]"
      }

    internal fun <T : SnakeContext> afterTurnMsg(
      context: T?,
      call: ApplicationCall,
      gameResponse: GameResponse,
      duration: Duration
    ): String =
      "Responded to ${call.request.uri} in $duration with: $gameResponse" +
          (context?.let { " [${context.snakeId}]" } ?: "")
  }
}
