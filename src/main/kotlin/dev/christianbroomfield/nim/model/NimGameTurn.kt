package dev.christianbroomfield.nim.model

data class NimGameTurn(
    val turn: Int,
    val matchSticksRemaining: Int,
    val matchSticksTaken: Int,
    val player: Player
)

enum class Player {
    HUMAN,
    AI
}