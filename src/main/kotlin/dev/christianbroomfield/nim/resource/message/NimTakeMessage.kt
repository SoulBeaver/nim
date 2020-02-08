package dev.christianbroomfield.nim.resource.message

import com.fasterxml.jackson.annotation.JsonProperty

data class NimTakeMessage(
    @JsonProperty("match_sticks_taken")
    val matchSticksTaken: Int
) {
    init {
        if (matchSticksTaken !in 1..3)
            throw IllegalArgumentException("Out of range, you must select [1, 2, 3] match sticks to take.")
    }
}