package dev.christianbroomfield.nim.app

data class NimConfiguration(
    val port: Int = 8080,
    val mongo: MongoConfiguration = MongoConfiguration()
)

data class MongoConfiguration(
    val host: String = "127.0.0.1",
    val port: Int = 27017
)