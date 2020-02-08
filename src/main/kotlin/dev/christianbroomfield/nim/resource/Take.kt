package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.model.Player
import dev.christianbroomfield.nim.resource.message.NimGameMessage
import dev.christianbroomfield.nim.resource.message.NimTakeMessage
import dev.christianbroomfield.nim.resource.message.toMessage
import dev.christianbroomfield.nim.service.Ai
import dev.christianbroomfield.nim.service.NimGameTurnService
import dev.christianbroomfield.nim.service.NimGameTurnResult
import dev.christianbroomfield.nim.service.Skynet
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

object Take {
    operator fun invoke(dao: NimGameDao, ai: Ai, nimGameTurnService: NimGameTurnService): RoutingHttpHandler {
        val nimGameIdPath = Path.string().of("id")

        val nimGameMsgLens = Body.auto<NimGameMessage>().toLens()
        val nimTakeMsgLens = Body.auto<NimTakeMessage>().toLens()

        val take: HttpHandler = { request ->
            val id = nimGameIdPath(request)
            val nimTakenMessage = nimTakeMsgLens(request)

            val nimGame = dao.get(id)
                ?.let { game ->
                    val partialGameTurn = nimGameTurnService
                        .take(game) { NimGameTurnResult(Player.HUMAN, nimTakenMessage.matchSticksTaken) }
                    val completedGameTurn = nimGameTurnService
                        .take(partialGameTurn) { ai.computeOptimalTurnStrategy(it) }

                    dao.update(id, completedGameTurn)
                }

            nimGame?.let {
                Response(Status.OK).with(nimGameMsgLens of it.toMessage())
            } ?: Response(Status.NOT_FOUND)
        }

        return "/{id:.*}/take" bind Method.POST to take
    }
}