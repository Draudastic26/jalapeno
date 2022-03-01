package com.draudastic

import com.draudastic.battlensake.Jalapeno
import com.draudastic.routing.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    install(ContentNegotiation) {
        json()
    }

    // Init your snake here 🐍
    val battleSnake = Jalapeno()
    configureRouting(battleSnake)
}
