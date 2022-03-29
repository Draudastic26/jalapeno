package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.test.assertContains


private val logger = KotlinLogging.logger {}

data class TestMove(val test: String, val request: MoveRequest, val acceptedMoves: Collection<Move>)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BattleSnakeTest {

    private val mapper = Json { ignoreUnknownKeys = true }
    private val requestPath = Path.of("src/test/resources/requests")

    private lateinit var testRequests: Collection<TestMove>

    private val defaultInfo = Info("TestSnake", "#ffffff", "default", "default", "1")

    @BeforeAll
    fun setup() {
        // All
        val files = requestPath.listDirectoryEntries().sortedBy { it.nameWithoutExtension.toInt() }
        // Specific request
//        val files = requestPath.listDirectoryEntries().sortedBy { it.nameWithoutExtension.toInt() }.filter { it.name == "10.move" }
        logger.info { "Loading ${files.count()} requests..." }
        val requests = mutableListOf<TestMove>()

        for (file in files) {
            val fileLines = Files.readAllLines(file)
            val acceptedMoves =
                mapper.decodeFromJsonElement<Collection<Move>>(mapper.parseToJsonElement(fileLines[0]).jsonObject["acceptedMoves"]!!)
            val request = mapper.decodeFromString<MoveRequest>(fileLines[1])
            requests.add(TestMove(file.name, request, acceptedMoves))
            logger.info { "Loaded ${file.name}..." }
        }
        testRequests = requests
    }

    private fun passRequest(): Collection<Arguments> {
        return testRequests.map {
            arguments(it.test, it.request, it.acceptedMoves)
        }
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("passRequest")
    fun `SimpleSnake should pass all requests`(name: String, request: MoveRequest, acceptedMoves: Collection<Move>) {
        // given
        val snake = SimpleSnake(defaultInfo)
        // when
        val response = snake.move(request)
        // then
        assertContains(acceptedMoves, response.move)
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("passRequest")
    fun `AggroSnake should pass all requests`(name: String, request: MoveRequest, acceptedMoves: Collection<Move>) {
        // given
        val snake = AggroSnake(defaultInfo)
        // when
        val response = snake.move(request)
        // then
        assertContains(acceptedMoves, response.move)
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("passRequest")
    fun `ChallengeSnake should pass all requests`(name: String, request: MoveRequest, acceptedMoves: Collection<Move>) {
        // given
        val snake = ChallengeSnake(defaultInfo)
        // when
        val response = snake.move(request)
        // then
        assertContains(acceptedMoves, response.move)
    }
}
