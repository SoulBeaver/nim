package dev.christianbroomfield.nim.unit.service

import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.NimGameTurn
import dev.christianbroomfield.nim.model.Player
import dev.christianbroomfield.nim.service.NimGameTurnService
import dev.christianbroomfield.nim.service.PlayerTurnResult
import io.kotlintest.shouldThrow
import io.kotlintest.specs.DescribeSpec
import org.amshove.kluent.shouldBeEqualTo

class NimGameTurnServiceSpec : DescribeSpec() {
    private val nimGameTurnService = NimGameTurnService()

    init {
        describe("Attempting to take too many matchsticks") {
            it("results in an exception") {
                shouldThrow<IllegalArgumentException> {
                    nimGameTurnService.take(NimGame.new()) {
                        PlayerTurnResult(Player.HUMAN, 6)
                    }
                }
            }
        }

        describe("Taking a valid turn") {
            it("Updates the game object and history") {
                val actual = nimGameTurnService.take(NimGame.new()) {
                    PlayerTurnResult(Player.HUMAN, 3)
                }

                actual shouldBeEqualTo NimGame(
                    id = actual.id,
                    turn = 2,
                    matchSticksRemaining = 10,
                    gameHistory = listOf(NimGameTurn(
                        turn = 1,
                        matchSticksRemaining = 13,
                        matchSticksTaken = 3,
                        player = Player.HUMAN
                    ))
                )
            }
        }

        describe("Taking multiple turns") {
            it("Updates the game object and history") {
                val turn1 = nimGameTurnService.take(NimGame.new()) {
                    PlayerTurnResult(Player.HUMAN, 3)
                }
                val turn2 = nimGameTurnService.take(turn1) {
                    PlayerTurnResult(Player.AI, 2)
                }
                val actual = nimGameTurnService.take(turn2) {
                    PlayerTurnResult(Player.HUMAN, 1)
                }

                actual shouldBeEqualTo NimGame(
                    id = actual.id,
                    turn = 4,
                    matchSticksRemaining = 7,
                    gameHistory = listOf(
                        NimGameTurn(
                            turn = 3,
                            matchSticksRemaining = 8,
                            matchSticksTaken = 1,
                            player = Player.HUMAN
                        ),
                        NimGameTurn(
                            turn = 2,
                            matchSticksRemaining = 10,
                            matchSticksTaken = 2,
                            player = Player.AI
                        ),
                        NimGameTurn(
                            turn = 1,
                            matchSticksRemaining = 13,
                            matchSticksTaken = 3,
                            player = Player.HUMAN
                        )
                    )
                )
            }
        }
    }
}