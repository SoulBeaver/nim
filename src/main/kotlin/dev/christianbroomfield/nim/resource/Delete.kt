package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object Delete {
    operator fun invoke(dao: NimGameDao): RoutingHttpHandler {
        val nimGameIdPath = Path.string().of("id")

        val delete: HttpHandler = { request ->
            val id = nimGameIdPath(request)

            dao.delete(id)?.let {
                Response(Status.OK)
            } ?: Response(Status.NOT_FOUND)
        }

        return "/{id:.*}" bind Method.DELETE to delete
    }
}