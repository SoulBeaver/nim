package dev.christianbroomfield.nim.model

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

data class NimGameTurn(
    val turn: Int,
    val matchSticksRemaining: Int,
    val matchSticksTaken: Int,
    val player: Player
) {
    init {
        // {{@see NimGame}} for an explanation for this particular code

        if (turn <= 0) {
            log.warn { "Attempted to write a negative turn value in the history; $turn" }
            throw IllegalArgumentException("Attempted to write a negative turn value in the history; $turn")
        }

        if (matchSticksRemaining !in MIN_MATCHSTICKS..MAX_MATCHSTICKS) {
            log.warn { "MatchSticksRemaining not in allowed range [$MIN_MATCHSTICKS..$MAX_MATCHSTICKS]; $matchSticksRemaining" }
            throw IllegalArgumentException("MatchSticksRemaining not in allowed range [$MIN_MATCHSTICKS..$MAX_MATCHSTICKS]; $matchSticksRemaining")
        }

        if (matchSticksTaken !in MIN_MATCHSTICKS_TO_TAKE..MAX_MATCHSTICKS_TO_TAKE) {
            log.warn { "MatchSticksTaken not in allowed range [$MIN_MATCHSTICKS_TO_TAKE..$MAX_MATCHSTICKS_TO_TAKE]; $matchSticksTaken" }
            throw IllegalArgumentException("MatchSticksTaken not in allowed range [$MIN_MATCHSTICKS_TO_TAKE..$MAX_MATCHSTICKS_TO_TAKE]; $matchSticksTaken")
        }
    }
}

enum class Player {
    HUMAN {
        override fun opponent() = AI
    },
    AI {
        override fun opponent() = HUMAN
    };

    abstract fun opponent(): Player
}