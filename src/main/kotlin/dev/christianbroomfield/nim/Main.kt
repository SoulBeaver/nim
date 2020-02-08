package dev.christianbroomfield.nim

import dev.christianbroomfield.nim.app.NimConfiguration
import dev.christianbroomfield.nim.app.NimServer
import mu.KotlinLogging
import org.http4k.server.Undertow
import org.http4k.server.asServer

private val log = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val configuration = NimConfiguration()

    NimServer(configuration).asServer(Undertow(configuration.port)).start().block()
}