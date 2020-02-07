package dev.christianbroomfield.nim

import dev.christianbroomfield.nim.app.NimServer
import mu.KotlinLogging
import org.http4k.server.Undertow
import org.http4k.server.asServer

private val log = KotlinLogging.logger {}

fun main(args: Array<String>) {
    NimServer().asServer(Undertow(8080)).start().block()
}