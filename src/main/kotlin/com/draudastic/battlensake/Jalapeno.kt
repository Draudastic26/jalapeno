package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse
import com.draudastic.models.Position

class Jalapeno : BattleSnake() {

    override val info = Info("Jalapeño", "#004d00", "pixel", "pixel", "1.0.0")

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        val avoidPositions = mutableSetOf<Position>().toHashSet()
        // avoid walls
        avoidPositions += wallPositions(moveRequest.board.width, moveRequest.board.height)
        // avoid hazards
        avoidPositions += moveRequest.board.hazards.map { it.position }
        // avoid snakes
        avoidPositions += moveRequest.board.snakes.flatMap { snake -> snake.body.map { body -> body.position } }

        var possibleMoves = mutableListOf(Move.Up, Move.Right, Move.Down, Move.Left)

        return MoveResponse(Move.Right)
    }

    private fun wallPositions(width: Int, height: Int): Collection<Position> {
        val walls = mutableListOf<Position>()
        for (x in 0 until width) walls += Position(x, -1)
        for (y in 0 until height) walls += Position(-1, y)
        return walls
    }
}
