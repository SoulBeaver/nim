package dev.christianbroomfield.nim.app

import dev.christianbroomfield.nim.dao.NimGameDao
import dev.christianbroomfield.nim.resource.NimResource
import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.HttpTransaction
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.litote.kmongo.KMongo

private val log = KotlinLogging.logger {}

object NimServer {
    operator fun invoke(): HttpHandler {
        val mongodb = KMongo.createClient("127.0.0.1", 27017)

        val nimGameDao = NimGameDao(mongodb)
        val nimResource = NimResource(nimGameDao)

        return assembleFilters().then(
            routes(
                "/" bind nimResource()
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