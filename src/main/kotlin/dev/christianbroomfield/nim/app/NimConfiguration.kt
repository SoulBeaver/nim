package dev.christianbroomfield.nim.app

import com.mongodb.MongoClientURI

/*
 * Dev note:  ideally we'd be using distributed configuration like Spring Cloud Config or even just reading
 * from environment variables or a yaml file.
 */
data class NimConfiguration(
    val debug: Boolean = true,
    val port: Int = 8080,
    val mongo: MongoConfiguration = MongoConfiguration()
)

data class MongoConfiguration(
    val uri: MongoClientURI = MongoClientURI(System.getenv("MONGODB_URL") ?: "mongodb://localhost:27017")
)