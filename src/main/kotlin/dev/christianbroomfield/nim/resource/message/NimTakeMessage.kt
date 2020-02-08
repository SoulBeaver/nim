package dev.christianbroomfield.nim.resource.message

import com.fasterxml.jackson.annotation.JsonProperty
import dev.christianbroomfield.nim.model.MAX_MATCHSTICKS_TO_TAKE
import dev.christianbroomfield.nim.model.MIN_MATCHSTICKS_TO_TAKE

data class NimTakeMessage(
    @JsonProperty("match_sticks_taken")
    val matchSticksTaken: Int
) {
    init {
        if (matchSticksTaken !in MIN_MATCHSTICKS_TO_TAKE..MAX_MATCHSTICKS_TO_TAKE)
            throw IllegalArgumentException("Out of range, you must select [1..3] match sticks to take.")
    }
}