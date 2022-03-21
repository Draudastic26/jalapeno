package com.draudastic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InfoResponse(
    val apiversion: String,
    val author: String,
    val color: String,
    val head: String,
    val tail: String,
    val version: String,
)

@Serializable
data class StartRequest(val game: Game, val turn: Int, val board: Board, val you: Snake)

@Serializable
data class EndRequest(val game: Game, val turn: Int, val board: Board, val you: Snake)

@Serializable
data class MoveRequest(val game: Game, val turn: Int, val board: Board, val you: Snake)

@Serializable
data class MoveResponse(val move: Move, val shout: String = "Going $move!")

@Serializable
enum class Move {
    @SerialName("up")
    Up,

    @SerialName("right")
    Right,

    @SerialName("down")
    Down,

    @SerialName("left")
    Left,
}

@Serializable
data class Game(val id: String, val ruleset: Ruleset, val timeout: Int, val source: String = "")

@Serializable
data class Ruleset(val name: String, val version: String, val settings: Settings = Settings())

@Serializable
data class Settings(
    val foodSpawnChance: Int = 10,
    val minimumFood: Int = 1,
    val hazardDamagePerTurn: Int = 1,
    val map: String = "default",
    val royale: Royale = Royale(),
    val squad: Squad = Squad()
)

@Serializable
data class Royale(val shrinkEveryNTurns: Int = 1)

@Serializable
data class Squad(
    val allowBodyCollisions: Boolean = true,
    val sharedElimination: Boolean = false,
    val sharedHealth: Boolean = false,
    val sharedLength: Boolean = false,
)

@Serializable
data class Board(
    val height: Int,
    val width: Int,
    val food: Collection<Food>,
    val hazards: Collection<Hazard> = emptyList(),
    val snakes: Collection<Snake>
)

@Serializable
data class Snake(
    val name: String,
    val id: String,
    val health: Int,
    val body: List<Body>,
    val latency: String = "",
    val head: Position = Position(0, 0),
    val length: Int = 0,
    val shout: String = "",
    val squad: String = "",
    val customizations: Customizations = Customizations("ffffff", "", ""),
) {
    fun bodyPosition(pos: Int) = body[pos].position
}

@Serializable
data class Customizations(val color: String, val head: String, val tail: String)

@Serializable
data class Body(val x: Int, val y: Int) {
    val position by lazy { Position(x, y) }
}

@Serializable
data class Food(val x: Int, val y: Int) {
    val position by lazy { Position(x, y) }
}

@Serializable
data class Hazard(val x: Int, val y: Int) {
    val position by lazy { Position(x, y) }
}

@Serializable
data class Position(val x: Int, val y: Int)
