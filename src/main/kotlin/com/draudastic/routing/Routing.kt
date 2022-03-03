package com.draudastic.routing

import com.draudastic.battlensake.BattleSnake
import com.draudastic.models.EndRequest
import com.draudastic.models.MoveRequest
import com.draudastic.models.StartRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(snake: BattleSnake) {
    routing {
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

fun Application.configureSnakesRouting(snakes: Collection<BattleSnake>) {
    routing {
        for (snake in snakes) {
            route("/${snake.info.name}") {
                get("/") {
                    call.respond(snake.info())
                }
            }
        }
    }
}