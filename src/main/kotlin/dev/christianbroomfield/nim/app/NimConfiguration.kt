package dev.christianbroomfield.nim.app

/*
 * Dev note:  ideally we'd be using distributed configuration like Spring Cloud Config or even just reading
 * from environment variables or a yaml file.
 */
data class NimConfiguration(
    val port: Int = 8080,
    val mongo: MongoConfiguration = MongoConfiguration()
)

data class MongoConfiguration(
    val host: String = "127.0.0.1",
    val port: Int = 27017
)