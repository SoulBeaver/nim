package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.unit.message.NimGameMessage
import dev.christianbroomfield.nim.unit.message.toMessage
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object GetCompleted {
    operator fun invoke(dao: NimGameDao): RoutingHttpHandler {
        val nimGamesMsgLens = Body.auto<List<NimGameMessage>>().toLens()

        val getCompleted: HttpHandler = { _ ->
            val nimGames = dao.getCompleted()
                .map { it.toMessage() }

            Response(Status.OK).with(nimGamesMsgLens of nimGames)
        }

        return "/completed" bind Method.GET to getCompleted
    }
}