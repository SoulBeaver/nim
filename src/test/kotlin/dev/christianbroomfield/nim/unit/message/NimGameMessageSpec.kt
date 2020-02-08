package dev.christianbroomfield.nim.unit.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.nim.model.Player
import dev.christianbroomfield.nim.util.fixture
import io.kotlintest.specs.DescribeSpec

class NimGameMessageSpec : DescribeSpec({
    val mapper = ObjectMapper().registerKotlinModule()

    describe("A Nim Game Message model") {
        val nimGame = NimGameMessage(
            turn = 1,
            matchSticksRemaining = 13,
            gameHistory = emptyList()
        )

        context("on serialization") {
            it("conforms to JSON representation") {
                val expected = mapper.writeValueAsString(
                    mapper.readValue(fixture("fixtures/nimGame.json"), NimGameMessage::class.java)
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
                    NimGameMessage::class.java
                )

                assertThat(
                    nimGame,
                    equalTo(expected))
            }
        }
    }

    describe("A Nim Game model with a couple of turns played") {
        val nimGame = NimGameMessage(
            turn = 3,
            matchSticksRemaining = 8,
            gameHistory = listOf(
                NimGameTurnMessage(
                    turn = 2,
                    matchSticksRemaining = 10,
                    matchSticksTaken = 2,
                    player = "AI"
                ),
                NimGameTurnMessage(
                    turn = 1,
                    matchSticksRemaining = 13,
                    matchSticksTaken = 3,
                    player = "HUMAN"
                )
            )
        )

        context("on serialization") {
            it("conforms to JSON representation") {
                val expected = mapper.writeValueAsString(
                    mapper.readValue(fixture("fixtures/nimGame-turn3.json"), NimGameMessage::class.java)
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
                    NimGameMessage::class.java
                )

                assertThat(
                    nimGame,
                    equalTo(expected))
            }
        }
    }

    describe("A finished Nim game") {
        val nimGame = NimGameMessage(
            turn = 3,
            winner = Player.HUMAN,
            matchSticksRemaining = 0,
            gameHistory = listOf()
        )

        context("on serialization") {
            it("conforms to JSON representation") {
                val expected = mapper.writeValueAsString(
                    mapper.readValue(fixture("fixtures/nimGame-completed.json"), NimGameMessage::class.java)
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
                    NimGameMessage::class.java
                )

                assertThat(
                    nimGame,
                    equalTo(expected))
            }
        }
    }
})