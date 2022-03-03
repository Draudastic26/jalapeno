package com.draudastic

import com.draudastic.battlensake.Info
import com.draudastic.battlensake.SimpleSnake
import com.draudastic.routing.configureRoutings
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

    // Init your snakes here 🐍
    val jalapeno = SimpleSnake(
        Info("Jalapeño", "#004d00", "pixel", "pixel", "1.0.0")
    )
    val habanero = SimpleSnake(
        Info("Habanero", "#c11e2b", "pixel", "pixel", "1.0.0")
    )
    val snakes = listOf(
        jalapeno,
        habanero
    )

    configureRoutings(snakes)
}
