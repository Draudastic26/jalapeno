package com.draudastic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DescribeResponse(
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
data class Game(val id: String, val ruleset: Ruleset, val timeout: Int)

@Serializable
data class Ruleset(val name: String, val version: String, val settings: Settings)

@Serializable
data class Settings(
    val foodSpawnChance: Int,
    val minimumFood: Int,
    val hazardDamagePerTurn: Int,
    val map: String,
    val royale: Royale,
    val squad: Squad
)

@Serializable
data class Royale(val shrinkEveryNTurns: Int)

@Serializable
data class Squad(
    val allowBodyCollisions: Boolean,
    val sharedElimination: Boolean,
    val sharedHealth: Boolean,
    val sharedLength: Boolean,
)

@Serializable
data class Board(
    val height: Int,
    val width: Int,
    val food: Collection<Food>,
    val hazards: Collection<Hazard>,
    val snakes: Collection<Snake>
)

@Serializable
data class Snake(
    val name: String,
    val id: String,
    val health: Int,
    val body: List<Body>,
    val latency: String,
    val head: Position,
    val length: Int,
    val shout: String,
    val squad: String = "",
    val customizations: Customizations,
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
