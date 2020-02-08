package dev.christianbroomfield.nim.service

import dev.christianbroomfield.nim.model.MAX_MATCHSTICKS_TO_TAKE
import dev.christianbroomfield.nim.model.MIN_MATCHSTICKS_TO_TAKE
import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.NimGameTurn
import dev.christianbroomfield.nim.model.Player
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class NimGameTurnService {
    fun take(nimGame: NimGame, playerAction: (NimGame) -> (PlayerTurnResult)): NimGame {
        val turnResult = playerAction(nimGame)

        require(turnResult.matchSticksTaken in MIN_MATCHSTICKS_TO_TAKE..MAX_MATCHSTICKS_TO_TAKE) {
            "Every turn has to result in $MIN_MATCHSTICKS_TO_TAKE..$MAX_MATCHSTICKS_TO_TAKE match sticks taken!"
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

data class PlayerTurnResult(
    val player: Player,
    val matchSticksTaken: Int
)