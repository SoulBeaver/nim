package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.model.NimGame
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

object Create {
    operator fun invoke(dao: NimGameDao): RoutingHttpHandler {
        val nimGameMsgLens = Body.auto<NimGameMessage>().toLens()

        val create: HttpHandler = { _ ->
            val newNimGame = dao.create(NimGame.new())

            Response(Status.CREATED)
                .header("Location", "/nim/${newNimGame.id}")
                .with(nimGameMsgLens of newNimGame.toMessage())
        }

        return "/" bind Method.POST to create
    }
}