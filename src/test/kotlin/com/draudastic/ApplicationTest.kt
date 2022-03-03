package com.draudastic

import com.draudastic.battlensake.Info
import com.draudastic.battlensake.SimpleSnake
import com.draudastic.routing.configureRoutings
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({
            configureRoutings(
                listOf(
                    SimpleSnake(
                        Info("Jalapeño", "#004d00", "pixel", "pixel", "1.0.0")
                    )
                )
            )
        }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("Hello World!", response.content)
            }
        }
    }
}
