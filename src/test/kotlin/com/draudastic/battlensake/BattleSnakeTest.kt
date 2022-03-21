package com.draudastic.battlensake

import com.draudastic.models.MoveRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.test.Test
import kotlin.test.assertTrue


private val logger = KotlinLogging.logger {}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BattleSnakeTest {

    private val mapper = Json { ignoreUnknownKeys = true }
    private val requestPath = Path.of("src/test/resources/requests")

    private fun readFileAsLinesUsingReadLines(file: Path): List<String> = Files.readAllLines(file)

    @BeforeAll
    fun setup() {
        logger.info { "Loading requests..." }
        val files = requestPath.listDirectoryEntries().sortedBy { it.nameWithoutExtension.toInt() }

        for (file in files) {
            val request = readFileAsLinesUsingReadLines(file)[1]
            val r = mapper.decodeFromString<MoveRequest>(request)
            logger.info { "${file.name} loaded..." }
        }
    }

    @Test
    fun `SimpleSnake should pass all requests`() {
        logger.info { "Hello" }
        assertTrue { true }
    }

    @Test
    fun `AggroSnake should pass all requests`() {
        logger.info { "Hello" }
        assertTrue { true }
    }
}
