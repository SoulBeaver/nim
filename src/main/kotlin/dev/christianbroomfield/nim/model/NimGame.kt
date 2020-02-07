package dev.christianbroomfield.nim.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NimGame(
    val id: Int,

    val turn: Int,
    val winner: Player? = null,

    @JsonProperty("match_sticks_remaining")
    val matchSticksRemaining: Int,

    @JsonProperty("game_history")
    val gameHistory: List<NimGameTurn>,

    val actions: List<NimGameAction>
)