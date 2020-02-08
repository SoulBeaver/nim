package dev.christianbroomfield.nim.unit.service

import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.Player
import dev.christianbroomfield.nim.service.Ai
import dev.christianbroomfield.nim.service.NimGameService
import dev.christianbroomfield.nim.service.NimGameTurnResult
import dev.christianbroomfield.nim.service.NimGameTurnService
import io.kotlintest.specs.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import org.amshove.kluent.shouldBeEqualTo

class NimGameServiceSpec : DescribeSpec() {
    private val nimGameTurnService = mockk<NimGameTurnService>(relaxed = true)
    private val ai = mockk<Ai>(relaxed = true)

    private val nimGameService = NimGameService(nimGameTurnService, ai)

    init {
        describe("Evaluating too many matchsticks taken") {
            val game = NimGame.new().copy(matchSticksRemaining = 1)

            it("returns an error message") {
                val result = nimGameService.take(game, 2)

                result shouldBeEqualTo NimGameTurnResult(
                    error = "Attempting to take more matchsticks than are left available"
                )
            }
        }

        describe("Attempting to take a turn for a finished game") {
            val game = NimGame.new().copy(winner = Player.HUMAN)

            it("returns an error message") {
                val result = nimGameService.take(game, 2)

                result shouldBeEqualTo NimGameTurnResult(
                    error = "Game is already complete"
                )
            }
        }

        describe("Taking a turn") {
            val game = NimGame.new()

            every {
                nimGameTurnService.take(any(), any())
            } returns game

            it("updates the game with both the Human and AI's turn") {
                val result = nimGameService.take(game, 2)

                result shouldBeEqualTo NimGameTurnResult(
                    updatedGame = game
                )

                verifyAll {
                    nimGameTurnService.take(any(), any())
                    nimGameTurnService.take(any(), any())
                }
            }
        }

        describe("Taking a losing turn") {
            val game = NimGame.new()

            every {
                nimGameTurnService.take(any(), any())
            } returns game.copy(winner = Player.HUMAN)

            it("only updates one turn") {
                val result = nimGameService.take(game, 2)

                result shouldBeEqualTo NimGameTurnResult(
                    updatedGame = game.copy(winner = Player.HUMAN)
                )

                // Only a single call, the other is skipped to return early
                verifyAll {
                    nimGameTurnService.take(any(), any())
                }
            }
        }
    }
}