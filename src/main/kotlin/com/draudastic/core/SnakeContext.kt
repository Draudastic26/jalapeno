@file:OptIn(ExperimentalTime::class)

package com.draudastic.core

import io.ktor.server.application.*
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

open class SnakeContext {
  private val clock = TimeSource.Monotonic
  private var gameStartTime = clock.markNow()

  var computeTime = 0.nanoseconds
    internal set

  var moveCount = 0L
    internal set

  lateinit var gameId: String
    internal set

  lateinit var snakeId: String
    internal set

  lateinit var call: ApplicationCall
    internal set

  internal fun resetStartTime() {
    gameStartTime = clock.markNow()
  }

  internal fun assignIds(gameId: String, snakeId: String) {
    this.gameId = gameId
    this.snakeId = snakeId
  }

  internal fun assignRequestResponse(call: ApplicationCall) {
    this.call = call
  }

  val elapsedGameTime get() = gameStartTime.elapsedNow()
}
