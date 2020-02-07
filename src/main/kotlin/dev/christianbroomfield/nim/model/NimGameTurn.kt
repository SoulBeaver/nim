package dev.christianbroomfield.nim.model

import com.fasterxml.jackson.annotation.JsonProperty

data class NimGameTurn(
    val turn: Int,
    @JsonProperty("match_sticks_remaining")
    val matchSticksRemaining: Int,
    @JsonProperty("match_sticks_taken")
    val matchSticksTaken: Int,
    val player: Player
)

enum class Player {
    HUMAN,
    AI
}