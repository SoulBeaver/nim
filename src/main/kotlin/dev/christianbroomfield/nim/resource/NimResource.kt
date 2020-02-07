package dev.christianbroomfield.nim.resource

import dev.christianbroomfield.nim.dao.NimGameDao
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes

class NimResource(private val nimGameDao: NimGameDao) {
    operator fun invoke() = routes(
        "/" bind Method.GET to getAll(),
        "/{id:.*}" bind Method.GET to get(),

        "/" bind Method.POST to create(),

        "/{id:.*}" bind Method.PUT to update(),

        "/{id:.*}" bind Method.DELETE to delete()
    )

    private fun getAll() = { _: Request ->
        val games = nimGameDao.getAll()

        Response(Status.OK).body("ping")
    }

    private fun get() = { _: Request ->
        val game = nimGameDao.get(1)
        Response(Status.OK).body("ping")
    }

    private fun create() = { _: Request ->
        Response(Status.OK).body("ping")
    }

    private fun update() = { _: Request ->
        Response(Status.OK).body("ping")
    }

    private fun delete() = { _: Request ->
        Response(Status.OK).body("ping")
    }
}