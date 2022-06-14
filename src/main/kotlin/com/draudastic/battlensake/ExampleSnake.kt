package com.draudastic.battlensake

import com.draudastic.core.AbstractBattleSnake
import com.draudastic.core.GameStrategy
import com.draudastic.core.SnakeContext
import com.draudastic.core.strategy
import com.draudastic.models.InfoResponse
import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import io.ktor.server.application.*

object ExampleSnake : AbstractBattleSnake<ExampleSnake.MySnakeContext>() {

    override fun gameStrategy(): GameStrategy<MySnakeContext> =
        strategy(verbose = true) {

            onStart { context, request ->
                logger.info { "START EXAMPLE SNAKE!" }
            }

            // DescribeResponse describes snake color and head/tail type
            onDescribe { call: ApplicationCall ->
                InfoResponse("1", "Draudastic", "#ff00ff", "beluga", "bolt")
            }

            // MoveResponse can be LEFT, RIGHT, UP or DOWN
            onMove { context: MySnakeContext, request: MoveRequest ->
//                logger.info { "My value = ${context.someValue}" }
                context.someValue++
                MoveResponse(Move.Up)
            }
        }

    // Called at the beginning of each game on Start for each snake
    override fun snakeContext(): MySnakeContext = MySnakeContext()

    // Add any necessary snake-specific data to the SnakeContext class
    class MySnakeContext : SnakeContext() {
        // Snake-specific context data goes here
        var someValue = 1
    }

}
