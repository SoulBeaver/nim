package dev.christianbroomfield.nim.service

import dev.christianbroomfield.nim.model.NimGame
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

// Dev note: we are deliberately avoiding a potential bug here:
// The regular flow creates a loop of AI-Human turns like so:

//     Turn: 1, Human
//     Turn: 2, AI
//     Turn: 3, Human

// It's completely possible that a person tries to use the update endpoint to create a wonky
// flow like

//     Turn: 1, Human
//     Turn: 2, Human
//     Turn: 3, Human

// Since we were manually put into a bad state and we're not trying to validate the history of turns,
// it's fine to use a simple method of once-twice undo instead of looking specifically for Human/AI turns.
// As well, sufficient Undoing will revert the game back into a healthy state (at latest back when it's a new game)
// so we're not breaking anything and the user is free to either continue or undo or PUT as he/she would like.
class UndoService {
    fun undo(nimGame: NimGame) = when {
        nimGame.gameHistory.isEmpty() ->
            nimGame // trying to undo a first turn is not exceptional, just return the object as-is

        nimGame.gameHistory.size.isOdd() -> {
            undoOnce(nimGame).also {
                log.debug { "Reverted from $nimGame to $it" }
            }
        }

        else -> {
            undoOnce(undoOnce(nimGame)).also {
                log.debug { "Reverted from $nimGame to $it" }
            }
        }
    }

    private fun undoOnce(nimGame: NimGame): NimGame {
        val previousTurnState = nimGame.gameHistory.first()

        return nimGame.copy(
            turn = previousTurnState.turn,
            matchSticksRemaining = previousTurnState.matchSticksRemaining,
            winner = null, // You can undo a completed game state
            gameHistory = nimGame.gameHistory.drop(1)
        )
    }
}

private fun <T> List<T>.second() = this.drop(1).first()

private fun Int.isOdd() = this % 2 == 1
private fun Int.isEven() = !this.isOdd()