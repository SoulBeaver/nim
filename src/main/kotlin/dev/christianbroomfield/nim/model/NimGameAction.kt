package dev.christianbroomfield.nim.model

data class NimGameAction(
    val type: NimGameActionType,
    val rel: String,
    val method: String,
    val payload: Map<String, Any>
)

enum class NimGameActionType {
    TAKE_ONE,
    TAKE_TWO,
    TAKE_THREE,

    UNDO
}