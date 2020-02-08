package dev.christianbroomfield.nim.dao

import com.mongodb.MongoClient
import dev.christianbroomfield.nim.model.NimGame
import mu.KotlinLogging
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.toId
import org.litote.kmongo.ne
import org.litote.kmongo.updateOne
import kotlin.collections.toList

private val log = KotlinLogging.logger {}

class NimGameDao(private val client: MongoClient) {
    private val nimGameDatabase = client.getDatabase("nimGames")
    private val nimGameCollection = nimGameDatabase.getCollection<NimGame>()

    fun getAll(): List<NimGame> {
        return nimGameCollection.find().toList().also {
            log.trace { "getAll: $it" }
        }
    }

    fun get(id: String): NimGame? {
        return nimGameCollection.findOne(NimGame::id eq ObjectId(id).toId()).also {
            log.debug { "get $id: $it" }
        }
    }

    fun getCompleted(): List<NimGame> {
        return nimGameCollection.find(NimGame::winner ne null).toList().also {
            log.debug { "getCompleted: $it" }
        }
    }

    fun getActive(): List<NimGame> {
        return nimGameCollection.find(NimGame::winner eq null).toList().also {
            log.debug { "getCompleted: $it" }
        }
    }

    fun create(nimGame: NimGame): NimGame {
        nimGameCollection.insertOne(nimGame)

        return nimGame.also {
            log.info { "inserted table: $it" }
        }
    }

    fun update(id: String, nimGame: NimGame): NimGame? {
        val objectId = ObjectId(id).toId<NimGame>()

        val updateResult = nimGameCollection.updateOne(NimGame::id eq objectId, nimGame.copy(id = objectId))

        return when (updateResult.modifiedCount) {
            1L -> nimGame
            else -> null
        }
    }

    fun delete(id: String): NimGame? {
        return nimGameCollection.findOneAndDelete(NimGame::id eq ObjectId(id).toId()).also {
            log.debug { "delete $id: $it" }
        }
    }
}