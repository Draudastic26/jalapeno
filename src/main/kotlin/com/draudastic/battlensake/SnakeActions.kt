package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.Position
import com.draudastic.utils.distance

class SnakeActions {

    fun getPossibleMoves(head: Position, avoidPositions: Collection<Position>): Collection<Move> {
        val possibleMoves = mutableListOf(Move.Up, Move.Right, Move.Down, Move.Left)
        Move.values().forEach { move ->
            val nextPos = head.getMovePosition(move)
            if (avoidPositions.contains(nextPos)) possibleMoves.remove(move)
        }
        return possibleMoves
    }

    fun moveTowards(head: Position, targetPosition: Position, possibleMoves: Collection<Move>): Move {
        return possibleMoves.minByOrNull { distance(head.getMovePosition(it), targetPosition) }
            ?: possibleMoves.randomOrNull() ?: Move.Up
    }
}
