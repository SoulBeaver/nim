package dev.christianbroomfield.nim.service

import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.NimGameTurn
import dev.christianbroomfield.nim.model.Player
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class NimGameTurnService {
    fun take(nimGame: NimGame, playerAction: (NimGame) -> (NimGameTurnResult)): NimGame {
        val turnResult = playerAction(nimGame)

        require(turnResult.matchSticksTaken in 1..3) {
            "Every turn has to result in 1..3 match sticks taken!"
        }

        val matchSticksRemaining = nimGame.matchSticksRemaining - turnResult.matchSticksTaken

        return nimGame.copy(
            turn = nimGame.turn + 1,
            matchSticksRemaining = matchSticksRemaining,
            gameHistory = listOf(NimGameTurn(
                turn = nimGame.turn,
                matchSticksTaken = turnResult.matchSticksTaken,
                matchSticksRemaining = nimGame.matchSticksRemaining,
                player = turnResult.player
            )) + nimGame.gameHistory,
            winner = if (matchSticksRemaining == 0) turnResult.player.opponent() else null
        )
    }
}

data class NimGameTurnResult(
    val player: Player,
    val matchSticksTaken: Int
)