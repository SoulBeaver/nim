package dev.christianbroomfield.nim.app

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.resource.Create
import dev.christianbroomfield.nim.resource.Delete
import dev.christianbroomfield.nim.resource.GetActive
import dev.christianbroomfield.nim.resource.GetAll
import dev.christianbroomfield.nim.resource.GetById
import dev.christianbroomfield.nim.resource.GetCompleted
import dev.christianbroomfield.nim.resource.Update
import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.HttpTransaction
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.litote.kmongo.KMongo
import org.litote.kmongo.id.jackson.IdJacksonModule

private val log = KotlinLogging.logger {}

object NimServer {
    operator fun invoke(configuration: NimConfiguration): HttpHandler {
        Jackson.mapper.registerModule(IdJacksonModule())

        val nimGameDao = NimGameDao(KMongo.createClient(configuration.mongo.host, configuration.mongo.port))

        /**
         * Dev note:  the way of doing things in Http4k is to keep every handler, a type of closure,
         * as slim as possible to avoid complicating the code. So while it makes sense to create a CRUD
         * class in Spring, here it's not considered best practice. Lenses are lightweight and easy to
         * create, so it's not considered harmful to duplicate Lenses if the same model is used in multiple
         * REST handlers.
         *
         * The added benefit is that you can test every handler in isolation. This wasn't done for these specific
         * handlers because the code is really *that* small that it could be handled in the integration test.
         */
        val getAllHandler = GetAll(nimGameDao)
        val getActiveHandler = GetActive(nimGameDao)
        val getCompletedHandler = GetCompleted(nimGameDao)
        val getByIdHandler = GetById(nimGameDao)

        val createHandler = Create(nimGameDao)
        val updateHandler = Update(nimGameDao)
        val deleteHandler = Delete(nimGameDao)

        return assembleFilters().then(
            routes(
                "/nim" bind routes(
                    getAllHandler,
                    getActiveHandler,
                    getCompletedHandler,
                    getByIdHandler,
                    createHandler,
                    updateHandler,
                    deleteHandler
                )
            )
        )
    }

    private fun assembleFilters(): Filter {
        val filter = DebuggingFilters
            .PrintRequestAndResponse()
            .then(ServerFilters.CatchLensFailure)

        return filter
            .then(ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
                log.info { "${tx.request.uri} ${tx.response.status}; took ${tx.duration.toMillis()}ms" }
            })
    }
}