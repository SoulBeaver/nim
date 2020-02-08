package dev.christianbroomfield.nim.integration.server

import dev.christianbroomfield.nim.app.MongoConfiguration
import dev.christianbroomfield.nim.app.NimConfiguration
import dev.christianbroomfield.nim.app.NimServer
import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.model.Player
import dev.christianbroomfield.nim.resource.message.NimGameMessage
import dev.christianbroomfield.nim.resource.message.NimGameTurnMessage
import dev.christianbroomfield.nim.resource.message.NimTakeMessage
import dev.christianbroomfield.nim.resource.message.toMessage
import dev.christianbroomfield.nim.util.EmbeddedMongoDB
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.kotlintest.specs.DescribeSpec
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldMatch
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.litote.kmongo.getCollection
import org.http4k.core.Body.Companion as Body1

class ServerIntegrationTest : DescribeSpec() {
    private val embeddedMongod = EmbeddedMongoDB()

    private val nimServer = NimServer(NimConfiguration(mongo = MongoConfiguration("localhost", 12345)))

    override fun listeners(): List<TestListener> {
        return listOf(embeddedMongod)
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)

        collection().drop()
    }

    init {
        describe("creating a new game") {
            val lens = Body.auto<NimGameMessage>().toLens()

            it("returns the new game and its location") {
                val response = nimServer(Request(Method.POST, "/nim"))

                response.status shouldBe Status.CREATED
                response.header("Location")!! shouldMatch """/nim/.*""".toRegex()
                lens(response) shouldBeEqualTo NimGame.new().toMessage()
            }
        }

        describe("getting all current games") {
            val lens = Body1.auto<List<NimGameMessage>>().toLens()

            context("with no games at all") {
                it("returns one entry for the new game") {
                    val response = nimServer(Request(Method.GET, "/nim"))

                    response.status shouldBe OK
                    lens(response) shouldBeEqualTo emptyList()
                }
            }

            context("with three games") {
                collection().insertMany(listOf(NimGame.new(), NimGame.new(), NimGame.new()))

                it("returns all three games") {
                    val response = nimServer(Request(Method.GET, "/nim"))

                    response.status shouldBe OK
                    lens(response) shouldBeEqualTo listOf(
                        NimGame.new().toMessage(),
                        NimGame.new().toMessage(),
                        NimGame.new().toMessage()
                    )
                }
            }
        }

        describe("getting all active games") {
            val lens = Body1.auto<List<NimGameMessage>>().toLens()

            context("with no active games") {
                it("returns an empty list") {
                    val response = nimServer(Request(Method.GET, "/nim/active"))

                    response.status shouldBe OK
                    lens(response) shouldBeEqualTo emptyList()
                }
            }

            context("with one active game") {
                collection().insertOne(NimGame.new())

                it("returns that active game") {
                    val response = nimServer(Request(Method.GET, "/nim/active"))

                    response.status shouldBe OK
                    lens(response) shouldBeEqualTo listOf(NimGame.new().toMessage())
                }
            }
        }

        describe("getting all completed games") {
            val lens = Body1.auto<List<NimGameMessage>>().toLens()

            context("with no completed games") {
                it("returns an empty list") {
                    val response = nimServer(Request(Method.GET, "/nim/completed"))

                    response.status shouldBe OK
                    lens(response) shouldBeEqualTo emptyList()
                }
            }

            context("with one completed game") {
                collection().insertOne(NimGame.completed())

                it("returns that completed game") {
                    val response = nimServer(Request(Method.GET, "/nim/completed"))

                    response.status shouldBe OK
                    lens(response) shouldBeEqualTo listOf(NimGame.completed().toMessage())
                }
            }
        }

        describe("deleting a new game") {
            val game = NimGame.new().also {
                collection().insertOne(it)
            }

            it("should remove the new entry") {
                val deleteResponse = nimServer(Request(Method.DELETE, "/nim/${game.id}"))
                deleteResponse.status shouldBe OK

                val getByIdResponse = nimServer(Request(Method.GET, "/nim/${game.id}"))
                getByIdResponse.status shouldBe NOT_FOUND
            }
        }

        describe("updating a game") {
            val lens = Body1.auto<NimGameMessage>().toLens()

            val game = NimGame.new().also {
                collection().insertOne(it)
            }

            it("should return the updated game") {
                val expected = NimGameMessage(
                    turn = 3,
                    matchSticksRemaining = 5,
                    gameHistory = emptyList()
                )
                val response = nimServer(Request(Method.PUT, "/nim/${game.id}")
                    .with(lens of expected))

                response.status shouldBe OK
                lens(response) shouldBeEqualTo expected
            }
        }

        describe("playing a game") {
            val gameLens = Body1.auto<NimGameMessage>().toLens()
            val takeLens = Body1.auto<NimTakeMessage>().toLens()

            val game = NimGame.new().also {
                collection().insertOne(it)
            }

            it("should update the game based on the Human and AI turns") {
                val response = nimServer(Request(Method.POST, "/nim/${game.id}/take")
                    .with(takeLens of NimTakeMessage(3)))

                response.status shouldBe OK

                val game = gameLens(response)
                game.turn shouldBeEqualTo 3
                // we introduced some randomness with the AI being able to take 1..3 match sticks
                // a clean test would account for this and even do property-based testing to exhaust multiple games at once
                game.gameHistory[1] shouldBeEqualTo NimGameTurnMessage(
                    turn = 1,
                    player = "HUMAN",
                    matchSticksRemaining = 13,
                    matchSticksTaken = 3
                )
            }
        }

        describe("undoing a fresh game") {
            val gameLens = Body1.auto<NimGameMessage>().toLens()

            val expected = NimGame.new().also {
                collection().insertOne(it)
            }

            it("returns the same game without an issue") {
                val response = nimServer(Request(Method.POST, "/nim/${expected.id}/undo"))
                response.status shouldBe OK

                gameLens(response) shouldBeEqualTo expected.toMessage()
            }
        }

        describe("undoing a game after one human turn (two turns total)") {
            val gameLens = Body1.auto<NimGameMessage>().toLens()
            val takeLens = Body1.auto<NimTakeMessage>().toLens()

            val expected = NimGame.new().also {
                collection().insertOne(it)
            }

            // perform a game turn
            nimServer(Request(Method.POST, "/nim/${expected.id}/take")
                .with(takeLens of NimTakeMessage(3)))

            it("reverts the game back to its fresh state") {
                val response = nimServer(Request(Method.POST, "/nim/${expected.id}/undo"))
                response.status shouldBe OK

                gameLens(response) shouldBeEqualTo expected.toMessage()
            }
        }
    }

    private fun collection() = embeddedMongod.mongoClient!!.getDatabase("nimGames").getCollection<NimGame>()

    private fun NimGame.Companion.completed() = NimGame.new().copy(winner = Player.HUMAN)
}