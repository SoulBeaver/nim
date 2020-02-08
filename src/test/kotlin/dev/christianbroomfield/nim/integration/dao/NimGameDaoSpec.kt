package dev.christianbroomfield.nim.integration.dao

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.util.EmbeddedMongoDB
import io.kotlintest.extensions.TestListener
import io.kotlintest.specs.DescribeSpec
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainAll
import org.litote.kmongo.getCollection

class NimGameDaoSpec : DescribeSpec() {
    private val embeddedMongoDB = EmbeddedMongoDB()

    override fun listeners(): List<TestListener> {
        return listOf(embeddedMongoDB)
    }

    init {
        describe("A NimGameDao") {
            val dao = NimGameDao(embeddedMongoDB.mongoClient!!)

            context("getting all objects in an empty database") {
                it("returns an empty list") {
                    val games = dao.getAll()

                    games.shouldBeEmpty()
                }
            }

            context("getting all objects in a populated database") {
                val newGames = listOf(NimGame.new(), NimGame.new())
                collection().insertMany(newGames)

                it("returns all objects") {
                    val games = dao.getAll()

                    games shouldContainAll newGames
                }
            }

            context("getting a single object by id that does not exist") {
                val game = NimGame.new() // Don't insert

                it("returns null") {
                    val game = dao.get(game.id.toString())

                    game.shouldBeNull()
                }
            }

            context("getting a single object by id") {
                val expected = NimGame.new().also {
                    collection().insertOne(it)
                }

                it("returns that object") {
                    val game = dao.get(expected.id.toString())

                    game shouldBeEqualTo expected
                }
            }

            context("creating a new object") {
                val newGame = NimGame.new()

                it("returns a new NimGame") {
                    val expected = dao.create(newGame)

                    newGame shouldBeEqualTo expected
                }
            }

            context("updating an object that does not exist") {
                val nimGame = NimGame.new() // Don't insert

                it("returns null") {
                    val updatedGame = dao.update(nimGame.id.toString(), nimGame.copy(turn = 3, matchSticksRemaining = 5))

                    updatedGame.shouldBeNull()
                }
            }

            context("updating an object") {
                val nimGame = NimGame.new().also {
                    collection().insertOne(it)
                }

                val expected = nimGame.copy(turn = 3, matchSticksRemaining = 5)

                it("returns the updated object") {
                    val updatedGame = dao.update(nimGame.id.toString(), expected)

                    updatedGame shouldBeEqualTo expected
                }
            }

            context("deleting an object that does not exist") {
                val nimGame = NimGame.new() // Don't insert

                it("returns null") {
                    val deletedGame = dao.delete(nimGame.id.toString())

                    deletedGame.shouldBeNull()
                }
            }

            context("deleting an object") {
                val expected = NimGame.new().also {
                    collection().insertOne(it)
                }

                it("returns null") {
                    val deletedGame = dao.delete(expected.id.toString())

                    deletedGame shouldBeEqualTo expected
                }
            }
        }
    }

    private fun collection() = embeddedMongoDB.mongoClient!!.getDatabase("nimGames").getCollection<NimGame>()
}