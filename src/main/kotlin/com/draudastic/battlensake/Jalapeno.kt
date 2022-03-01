package com.draudastic.battlensake

import com.draudastic.models.Move
import com.draudastic.models.MoveRequest
import com.draudastic.models.MoveResponse

class Jalapeno : BattleSnake() {

    override val appearance = Appearance("Jalapeño", "#004d00", "pixel", "pixel")

    override fun decideMove(moveRequest: MoveRequest): MoveResponse {
        return MoveResponse(Move.Right)
    }
}
