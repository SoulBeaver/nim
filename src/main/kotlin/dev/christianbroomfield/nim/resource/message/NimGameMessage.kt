package dev.christianbroomfield.nim.resource.message

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.NimGameTurn
import dev.christianbroomfield.nim.model.Player
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NimGameMessage(
    val turn: Int,
    val winner: Player? = null,

    @JsonProperty("match_sticks_remaining")
    val matchSticksRemaining: Int,

    @JsonProperty("game_history")
    val gameHistory: List<NimGameTurnMessage>
)

data class NimGameTurnMessage(
    val turn: Int,
    @JsonProperty("match_sticks_remaining")
    val matchSticksRemaining: Int,
    @JsonProperty("match_sticks_taken")
    val matchSticksTaken: Int,
    val player: String
)

fun NimGame.toMessage() = NimGameMessage(
    turn = this.turn,
    winner = this.winner,
    matchSticksRemaining = this.matchSticksRemaining,
    gameHistory = this.gameHistory.map { it.toMessage() }
)

fun NimGameTurn.toMessage() = NimGameTurnMessage(
    turn = this.turn,
    matchSticksRemaining = this.matchSticksRemaining,
    matchSticksTaken = this.matchSticksTaken,
    player = this.player.name
)

fun NimGameMessage.toModel(id: String) = NimGame(
    id = ObjectId(id).toId(),
    turn = this.turn,
    winner = this.winner,
    matchSticksRemaining = this.matchSticksRemaining,
    gameHistory = this.gameHistory.map { it.toModel() }
)

fun NimGameTurnMessage.toModel() = NimGameTurn(
    turn = this.turn,
    matchSticksRemaining = this.matchSticksRemaining,
    matchSticksTaken = this.matchSticksTaken,
    player = Player.valueOf(this.player)
)