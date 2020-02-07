package dev.christianbroomfield.nim.dao

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import dev.christianbroomfield.nim.model.NimGame
import io.kotlintest.Spec
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.specs.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import org.litote.kmongo.getCollection

class NimGameDaoSpec : DescribeSpec() {
    private val mongoClient = mockk<MongoClient>()
    private val database = mockk<MongoDatabase>()
    private val collection = mockk<MongoCollection<NimGame>>()

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        every { mongoClient.getDatabase("nimGames") } returns database
        every { database.getCollection<NimGame>() } returns collection
    }

    // Init required here because we're overriding the behavior of beforeSpecClass. This would not be possible
    // if we tried to use the local method syntax `DescribeSpec({})`
    init {
        describe("A NimGameDao") {
            context("getting all objects") {
            }
            context("getting a single object") {
                context("that does not exist") {
                    it("returns null") {
                    }
                }
            }
            context("creating a new object") {
                context("with a conflicting id") {
                    it("throws an exception") {
                    }
                }
            }
            context("updating an object") {
            }
            context("deleting an object") {
            }
        }
    }
}