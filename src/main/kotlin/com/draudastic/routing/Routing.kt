package com.draudastic.routing

import com.draudastic.battlensake.BattleSnake
import com.draudastic.battlensake.Info
import com.draudastic.core.AbstractBattleSnake
import com.draudastic.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutings(snakes: Collection<BattleSnake>, newSnake: AbstractBattleSnake<*>) {
    routing {
        for (snake in snakes) {
            route("/${snake.info.name}") {
                get("/") {
                    call.respond(snake.info())
                }
                post("/start") {
                    val request = call.receive<StartRequest>()
                    snake.start(request)
                    call.respond(HttpStatusCode.OK)
                }
                post("/move") {
                    val request = call.receive<MoveRequest>()
                    call.respond(snake.move(request))
                }
                post("/end") {
                    val request = call.receive<EndRequest>()
                    snake.end(request)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        route("/new") {
            get("/") {
                val response = newSnake.process(call) as InfoResponse
                call.respond(response)
            }
            post("/start") {
                val response = newSnake.process(call) as StartResponse
                call.respond(response)
            }
            post("/move") {
                val response = newSnake.process(call) as MoveResponse
                call.respond(response)
            }
            post("/end") {
                val response = newSnake.process(call) as EndResponse
                call.respond(response)
            }
        }

    }
}
