package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.model.NimGame
import dev.christianbroomfield.nim.resource.message.NimGameMessage
import dev.christianbroomfield.nim.resource.message.NimTakeMessage
import dev.christianbroomfield.nim.resource.message.toMessage
import dev.christianbroomfield.nim.service.NimGameService
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

private val log = KotlinLogging.logger {}

/*
 * Dev note: this performs the core game loop between Human and PC. You can try it out in the requests.http!
 *
 * You might disagree with my decision to keep the DAO on the top level. A frequent argument I hear is that you should
 * keep business and database logic separate from the REST layer. And while yes generally I'd agree, it's a lot easier
 * to test if the business logic is separate from the database logic.
 */
object Take {
    operator fun invoke(dao: NimGameDao, nimGameService: NimGameService): RoutingHttpHandler {
        val nimGameIdPath = Path.string().of("id")

        val nimGameMsgLens = Body.auto<NimGameMessage>().toLens()
        val nimTakeMsgLens = Body.auto<NimTakeMessage>().toLens()

        val take: HttpHandler = { request ->
            val id = nimGameIdPath(request)
            val nimTakenMessage = nimTakeMsgLens(request)

            dao.get(id)?.let { game ->
                val updateGameResult = nimGameService.take(game, nimTakenMessage.matchSticksTaken)

                when {
                    updateGameResult.error != null ->
                        Response(Status.BAD_REQUEST).body(updateGameResult.error)
                    else -> {
                        // Ensure that we only update once for both game turns to prevent a player
                        // from inputting their turn between game turns
                        dao.update(updateGameResult.updatedGame!!.id.toString(), updateGameResult.updatedGame)
                        Response(Status.OK).with(nimGameMsgLens of updateGameResult.updatedGame.toMessage())
                    }
                }
            } ?: Response(Status.NOT_FOUND)
        }

        return "/{id:.*}/take" bind Method.POST to take
    }

    private fun isValidTurn(game: NimGame, matchSticksTaken: Int): Boolean =
        game.matchSticksRemaining < matchSticksTaken
}