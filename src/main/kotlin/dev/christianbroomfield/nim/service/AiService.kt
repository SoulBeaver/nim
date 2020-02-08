package dev.christianbroomfield.nim.service

import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.Player
import mu.KotlinLogging
import java.lang.IllegalArgumentException
import kotlin.random.Random

private val log = KotlinLogging.logger {}

interface Ai {
    fun computeOptimalTurnStrategy(nimGame: NimGame): NimGameTurnResult
}

class Skynet(private val random: Random = Random.Default): Ai {
    override fun computeOptimalTurnStrategy(nimGame: NimGame) =
        NimGameTurnResult(Player.AI, determineMatchSticksToTake(nimGame.matchSticksRemaining))

    private fun determineMatchSticksToTake(matchSticksRemaining: Int) = when {
        // Doesn't really matter
        matchSticksRemaining > 8 -> random.nextInt(1, 4)

        // We really want to reach 5
        matchSticksRemaining in 6..8 -> matchSticksRemaining - 5

        // Eh, can't really win anymore here
        matchSticksRemaining == 5 -> 1

        // Delay as long as possible out of spite :)
        matchSticksRemaining in 2..4 -> matchSticksRemaining - 1

        // Lose gracefully
        matchSticksRemaining == 1 -> {
            log.info { "Well played, Connor..." }
            1
        }

        else ->
            throw IllegalArgumentException("Match Sticks Remaining doesn't conform to a value between [0, 13]: $matchSticksRemaining")
    }
}
