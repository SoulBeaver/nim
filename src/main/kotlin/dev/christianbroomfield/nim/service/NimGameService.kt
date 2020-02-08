package dev.christianbroomfield.nim.service

import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.Player

/*
 * Dev note: Poor man's Either. If this were a larger project it would be worth bringing in Arrow!
 */
data class NimGameTurnResult(val error: String? = null, val updatedGame: NimGame? = null) {
    companion object {
        fun Left(error: String) = NimGameTurnResult(error)
        fun Right(updatedGame: NimGame) = NimGameTurnResult(updatedGame = updatedGame)
    }
}

class NimGameService(
    private val nimGameTurnService: NimGameTurnService,
    private val ai: Ai
) {

    fun take(activeGame: NimGame, matchSticksTaken: Int): NimGameTurnResult {
        if (isComplete(activeGame)) {
            return NimGameTurnResult.Left("Game is already complete")
        }
        if (activeGame.matchSticksRemaining < matchSticksTaken) {
            return NimGameTurnResult.Left("Attempting to take more matchsticks than are left available")
        }

        val humanGameTurn = nimGameTurnService
            .take(activeGame) { PlayerTurnResult(Player.HUMAN, matchSticksTaken) }

        if (isComplete(humanGameTurn)) {
            return NimGameTurnResult.Right(humanGameTurn)
        }

        val aiGameTurn = nimGameTurnService
            .take(humanGameTurn) { ai.computeOptimalTurnStrategy(it) }

        return NimGameTurnResult.Right(aiGameTurn)
    }

    private fun isComplete(game: NimGame) = game.winner != null
}