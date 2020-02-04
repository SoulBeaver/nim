package dev.christianbroomfield.nim

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

fun main(args: Array<String>) {
    log.info {
        "Server started."
    }
}