package dev.christianbroomfield.nim.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class NimGame(
    @BsonId
    val id: Id<NimGame> = newId(),

    val turn: Int,
    val winner: Player? = null,

    val matchSticksRemaining: Int,

    val gameHistory: List<NimGameTurn>
) {
    companion object {
        fun new() = NimGame(
            turn = 1,
            matchSticksRemaining = 13,
            gameHistory = emptyList()
        )
    }
}