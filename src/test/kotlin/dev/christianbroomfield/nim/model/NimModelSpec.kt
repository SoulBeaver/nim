package dev.christianbroomfield.nim.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.nim.util.fixture
import io.kotlintest.specs.DescribeSpec

class NimModelSpec : DescribeSpec({
    val mapper = ObjectMapper().registerKotlinModule()

    describe("A Nim Game model") {
        val nimGame = NimGame(
            id = 1,
            turn = 1,
            matchSticksRemaining = 13,
            gameHistory = emptyList(),
            actions = listOf(
                NimGameAction(
                    type = NimGameActionType.TAKE_ONE,
                    rel = "/nim/1/take",
                    method = "PUT",
                    payload = mapOf(
                        "turn" to 1,
                        "match_sticks_taken" to 1
                    )
                ),
                NimGameAction(
                    type = NimGameActionType.TAKE_TWO,
                    rel = "/nim/1/take",
                    method = "PUT",
                    payload = mapOf(
                        "turn" to 1,
                        "match_sticks_taken" to 2
                    )
                ),
                NimGameAction(
                    type = NimGameActionType.TAKE_THREE,
                    rel = "/nim/1/take",
                    method = "PUT",
                    payload = mapOf(
                        "turn" to 1,
                        "match_sticks_taken" to 3
                    )
                )
            )
        )

        context("on serialization") {
            it("conforms to JSON representation") {
                val expected = mapper.writeValueAsString(
                    mapper.readValue(fixture("fixtures/nimGame.json"), NimGame::class.java)
                )

                assertThat(
                    mapper.writeValueAsString(nimGame),
                    equalTo(expected)
                )
            }
        }

        context("on deserialization") {
            it("maps to the Kotlin/Java class") {
                val expected = mapper.readValue(
                    fixture("fixtures/nimGame.json"),
                    NimGame::class.java
                )

                assertThat(
                    nimGame,
                    equalTo(expected))
            }
        }
    }

    describe("A Nim Game model with a couple of turns played") {
        val nimGame = NimGame(
            id = 1,
            turn = 3,
            matchSticksRemaining = 8,
            gameHistory = listOf(
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
            ),
            actions = listOf(
                NimGameAction(
                    type = NimGameActionType.TAKE_ONE,
                    rel = "/nim/1/take",
                    method = "PUT",
                    payload = mapOf(
                        "turn" to 3,
                        "match_sticks_taken" to 1
                    )
                ),
                NimGameAction(
                    type = NimGameActionType.TAKE_TWO,
                    rel = "/nim/1/take",
                    method = "PUT",
                    payload = mapOf(
                        "turn" to 3,
                        "match_sticks_taken" to 2
                    )
                ),
                NimGameAction(
                    type = NimGameActionType.TAKE_THREE,
                    rel = "/nim/1/take",
                    method = "PUT",
                    payload = mapOf(
                        "turn" to 3,
                        "match_sticks_taken" to 3
                    )
                ),
                NimGameAction(
                    type = NimGameActionType.UNDO,
                    rel = "/nim/1/undo",
                    method = "PUT",
                    payload = mapOf(
                        "turn" to 3
                    )
                )
            )
        )

        context("on serialization") {
            it("conforms to JSON representation") {
                val expected = mapper.writeValueAsString(
                    mapper.readValue(fixture("fixtures/nimGame-turn3.json"), NimGame::class.java)
                )

                assertThat(
                    mapper.writeValueAsString(nimGame),
                    equalTo(expected)
                )
            }
        }

        context("on deserialization") {
            it("maps to the Kotlin/Java class") {
                val expected = mapper.readValue(
                    fixture("fixtures/nimGame-turn3.json"),
                    NimGame::class.java
                )

                assertThat(
                    nimGame,
                    equalTo(expected))
            }
        }
    }

    describe("A finished Nim game") {
        val nimGame = NimGame(
            id = 1,
            turn = 3,
            winner = Player.HUMAN,
            matchSticksRemaining = 0,
            gameHistory = listOf(),
            actions = listOf()
        )

        context("on serialization") {
            it("conforms to JSON representation") {
                val expected = mapper.writeValueAsString(
                    mapper.readValue(fixture("fixtures/nimGame-completed.json"), NimGame::class.java)
                )

                assertThat(
                    mapper.writeValueAsString(nimGame),
                    equalTo(expected)
                )
            }
        }

        context("on deserialization") {
            it("maps to the Kotlin/Java class") {
                val expected = mapper.readValue(
                    fixture("fixtures/nimGame-completed.json"),
                    NimGame::class.java
                )

                assertThat(
                    nimGame,
                    equalTo(expected))
            }
        }
    }
})