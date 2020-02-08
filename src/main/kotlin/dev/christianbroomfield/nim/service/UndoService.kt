package dev.christianbroomfield.nim.service

import dev.christianbroomfield.nim.model.NimGame
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class UndoService {
    fun undo(nimGame: NimGame) = when {
        nimGame.gameHistory.isNotEmpty() -> {
            val previousTurnState = nimGame.gameHistory.second()

            nimGame.copy(
                turn = previousTurnState.turn,
                matchSticksRemaining = previousTurnState.matchSticksRemaining,
                winner = null, // You can undo a completed game state
                gameHistory = nimGame.gameHistory.drop(2)
            ).also {
                log.debug { "Reverted from $nimGame to $it" }
            }
        }

        else -> nimGame // trying to undo a first turn is not exceptional, just return the object as-s
    }
}

private fun <T> List<T>.second() = this.drop(1).first()