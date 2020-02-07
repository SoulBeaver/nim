package dev.christianbroomfield.nim.dao

import com.mongodb.MongoClient
import dev.christianbroomfield.nim.model.NimGame
import mu.KotlinLogging
import org.litote.kmongo.createIndex
import org.litote.kmongo.eq
import org.litote.kmongo.ne
import org.litote.kmongo.findOne
import org.litote.kmongo.updateOne
import org.litote.kmongo.getCollection
import kotlin.collections.toList

private val log = KotlinLogging.logger {}

class NimGameDao(private val client: MongoClient) {
    private val nimGameDatabase = client.getDatabase("nimGames")
    private val nimGameCollection = nimGameDatabase.getCollection<NimGame>()

    init {
        nimGameCollection.createIndex("{id:1}, {unique:true}")
    }

    fun getAll(): List<NimGame> {
        return nimGameCollection.find().toList().also {
            log.trace { "getAll: $it" }
        }
    }

    fun get(id: Int): NimGame? {
        return nimGameCollection.findOne(NimGame::id eq id).also {
            log.debug { "get $id: $it" }
        }
    }

    fun getCompleted(): List<NimGame> {
        return nimGameCollection.find(NimGame::winner ne null).toList().also {
            log.debug { "getCompleted: $it" }
        }
    }

    fun create(nimGame: NimGame): NimGame {
        nimGameCollection.insertOne(nimGame)

        return nimGame.also {
            log.debug { "inserted table: $it" }
        }
    }

    fun update(id: Int, nimGame: NimGame): NimGame {
        val updateResult = nimGameCollection.updateOne(NimGame::id eq id)

        return nimGame.also {
            log.debug { "updated table: $it" }
        }
    }

    fun delete(id: Int): NimGame? {
        return nimGameCollection.findOneAndDelete(NimGame::id eq id).also {
            log.debug { "delete $id: $it" }
        }
    }
}