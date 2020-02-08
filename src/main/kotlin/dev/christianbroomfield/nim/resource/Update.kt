package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.unit.message.NimGameMessage
import dev.christianbroomfield.nim.unit.message.toMessage
import dev.christianbroomfield.nim.unit.message.toModel
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

object Update {
    operator fun invoke(dao: NimGameDao): RoutingHttpHandler {
        val nimGameIdPath = Path.string().of("id")
        val nimGameMsgLens = Body.auto<NimGameMessage>().toLens()

        val update: HttpHandler = { request ->
            val id = nimGameIdPath(request)
            val updatedNimGame = nimGameMsgLens(request).toModel(id)

            dao.update(id, updatedNimGame)?.let {
                Response(Status.OK).with(nimGameMsgLens of it.toMessage())
            } ?: Response(Status.NOT_FOUND)
        }

        return "/{id:.*}" bind Method.PUT to update
    }
}