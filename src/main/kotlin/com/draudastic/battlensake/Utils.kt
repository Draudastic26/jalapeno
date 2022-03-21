package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.Position
import com.draudastic.models.Snake

fun Position.getWrappedPositions(width: Int, height: Int): Collection<Position> {
    return listOf(
        Position(this.x, this.y + height),
        Position(this.x + width, this.y),
        Position(this.x, this.y - height),
        Position(this.x - width, this.y)
    )
}

fun Snake.isLargestSnake(snakes: Collection<Snake>): Boolean {
    val otherSnakesLargerOrSame = snakes.filter { it.id != this.id && it.length >= this.length }
    return otherSnakesLargerOrSame.isEmpty()
}

fun Position.getMovePosition(direction: Move): Position {
    return when (direction) {
        Move.Up -> Position(this.x, this.y + 1)
        Move.Right -> Position(this.x + 1, this.y)
        Move.Down -> Position(this.x, this.y - 1)
        Move.Left -> Position(this.x - 1, this.y)
    }
}

fun Position.getAllMovePositions(): Collection<Position> {
    return Move.values().map { this.getMovePosition(it) }
}
