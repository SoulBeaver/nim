package dev.christianbroomfield.nim.unit.service

import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.NimGameTurn
import dev.christianbroomfield.nim.model.Player
import dev.christianbroomfield.nim.service.UndoService
import io.kotlintest.specs.DescribeSpec
import org.amshove.kluent.shouldBeEqualTo

class UndoServiceSpec: DescribeSpec() {
    private val undoService = UndoService()

    init {
        describe("A new game of Nim") {
            val nimGame = NimGame.new()

            it("can't undo the first turn") {
                val undoneNimGame = undoService.undo(nimGame)

                undoneNimGame shouldBeEqualTo NimGame.new().copy(id=nimGame.id) // need the id for equality
            }
        }

        describe("A game of Nim after a player's first turn") {
            val nimGame = NimGame(
                turn = 3,
                matchSticksRemaining = 7,
                gameHistory = listOf(
                    NimGameTurn(turn = 2, matchSticksRemaining = 10, matchSticksTaken = 3, player = Player.AI),
                    NimGameTurn(turn = 1, matchSticksRemaining = 13, matchSticksTaken = 3, player = Player.HUMAN)
                )
            )

            it("will revert back to the start of the game") {
                val expected = NimGame.new().copy(id = nimGame.id)

                val actual = undoService.undo(nimGame)

                actual shouldBeEqualTo expected
            }
        }

        describe("A completed game of Nim") {
            val nimGame = NimGame(
                turn = 6,
                matchSticksRemaining = 0,
                winner = Player.AI,
                gameHistory = listOf(
                    NimGameTurn(turn = 5, matchSticksRemaining = 1, matchSticksTaken = 1, player = Player.HUMAN),
                    NimGameTurn(turn = 4, matchSticksRemaining = 4, matchSticksTaken = 3, player = Player.AI),
                    NimGameTurn(turn = 3, matchSticksRemaining = 7, matchSticksTaken = 3, player = Player.HUMAN),
                    NimGameTurn(turn = 2, matchSticksRemaining = 10, matchSticksTaken = 3, player = Player.AI),
                    NimGameTurn(turn = 1, matchSticksRemaining = 13, matchSticksTaken = 3, player = Player.HUMAN)
                )
            )

            it("reverts back one turn") {
                val expected = NimGame(
                    id = nimGame.id,
                    turn = 4,
                    matchSticksRemaining = 4,
                    gameHistory = listOf(
                        NimGameTurn(turn = 3, matchSticksRemaining = 7, matchSticksTaken = 3, player = Player.HUMAN),
                        NimGameTurn(turn = 2, matchSticksRemaining = 10, matchSticksTaken = 3, player = Player.AI),
                        NimGameTurn(turn = 1, matchSticksRemaining = 13, matchSticksTaken = 3, player = Player.HUMAN)
                    )
                )

                val actual = undoService.undo(nimGame)

                actual shouldBeEqualTo expected
            }
        }
    }

}