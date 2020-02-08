package dev.christianbroomfield.nim.model

import mu.KotlinLogging
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

private val log = KotlinLogging.logger {}

const val MAX_MATCHSTICKS = 13
const val MIN_MATCHSTICKS = 0

const val MAX_MATCHSTICKS_TO_TAKE = 3
const val MIN_MATCHSTICKS_TO_TAKE = 1

data class NimGame(
    @BsonId
    val id: Id<NimGame> = newId(),

    val turn: Int,
    val winner: Player? = null,

    val matchSticksRemaining: Int,

    val gameHistory: List<NimGameTurn>
) {
    init {
        // Dev note: Because http4k uses lenses the regular JSON validation doesn't work out of the box.
        // Instead, the author recommends to convert exceptions into Lens failures which are returned as 400 by the server.

        // Unfortunately, a bug in the http4k code prevents us from seeing what the validation error is
        // so we need to manually log the validation error if we want to have any chance of quickly
        // identifying the issue.

        if (turn <= 0) {
            log.warn { "Attempted to write a negative turn value; $turn" }
            throw IllegalArgumentException("Attempted to write a negative turn value; $turn")
        }

        if (matchSticksRemaining !in MIN_MATCHSTICKS..MAX_MATCHSTICKS) {
            log.warn { "MatchSticksRemaining not in allowed range [$MIN_MATCHSTICKS..$MAX_MATCHSTICKS]; $matchSticksRemaining" }
            throw IllegalArgumentException("MatchSticksRemaining not in allowed range [$MIN_MATCHSTICKS..$MAX_MATCHSTICKS]; $matchSticksRemaining")
        }
    }

    companion object {
        fun new() = NimGame(
            turn = 1,
            matchSticksRemaining = MAX_MATCHSTICKS,
            gameHistory = emptyList()
        )
    }
}