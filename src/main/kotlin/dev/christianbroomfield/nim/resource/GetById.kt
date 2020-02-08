package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
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

object GetById {
    operator fun invoke(dao: NimGameDao): RoutingHttpHandler {
        val nimGameIdPath = Path.string().of("id")
        val nimGameLens = Body.auto<NimGameMessage>().toLens()

        val getById: HttpHandler = { request ->
            val id = nimGameIdPath(request)

            dao.get(id)?.let {
                Response(Status.OK).with(nimGameLens of it.toMessage())
            } ?: Response(Status.NOT_FOUND)
        }

        return "/{id:.*}" bind Method.GET to getById
    }
}