package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.service.UndoService
import dev.christianbroomfield.nim.resource.message.NimGameMessage
import dev.christianbroomfield.nim.resource.message.toMessage
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

/*
 * Dev note: I added UNDO out of convenience and because I didn't like the idea of scrapping and recreating entries
 * in the MongoDB every time I finished testing a game. It adds a little extra complexity, but overall the changes
 * weren't excessive as long as I maintained statelessness and immutability, both of which REST and Kotlin idioms
 * excel at. I also feel more at ease sacrificing some speed because the framework itself is pretty fast and we can
 * "afford" to lose some CPU cycles copying and lensing.
 */
object Undo {
    operator fun invoke(dao: NimGameDao, undoService: UndoService): RoutingHttpHandler {
        val nimGameIdPath = Path.string().of("id")
        val nimGameMsgLens = Body.auto<NimGameMessage>().toLens()

        val undo: HttpHandler = { request ->
            val id = nimGameIdPath(request)

            val undoneGame = dao.get(id)?.let {
                undoService.undo(it)
            }?.also {
                dao.update(id, it)
            }

            undoneGame?.let {
                Response(Status.OK).with(nimGameMsgLens of it.toMessage())
            } ?: Response(Status.NOT_FOUND)
        }

        return "/{id:.*}/undo" bind Method.POST to undo
    }
}