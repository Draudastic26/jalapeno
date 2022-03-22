package com.draudastic

import com.draudastic.battlensake.AggroSnake
import com.draudastic.battlensake.Info
import com.draudastic.battlensake.SimpleSnake
import com.draudastic.routing.configureRoutings
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Post)
        method(HttpMethod.Get)
        header(HttpHeaders.ContentType)
        anyHost()
    }

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    // Init your snakes here 🐍
    val jalapeno = SimpleSnake(
        Info("Jalapeno", "#004d00", "pixel", "pixel", "1.0.0")
    )
    val habanero = AggroSnake(
        Info("Habanero", "#c11e2b", "pixel", "pixel", "1.0.0")
    )
    val snakes = listOf(
        jalapeno,
        habanero
    )

    configureRoutings(snakes)
}
