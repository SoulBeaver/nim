package dev.christianbroomfield.nim.unit.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.christianbroomfield.nim.resource.message.NimTakeMessage
import dev.christianbroomfield.nim.util.fixture
import io.kotlintest.specs.DescribeSpec

class NimTakeMessageSpec : DescribeSpec({
    val mapper = ObjectMapper().registerKotlinModule()

    describe("A Nim Take Message model") {
        val nimTakeMessage = NimTakeMessage(matchSticksTaken = 1)

        it("on serialization") {
            val expected = mapper.writeValueAsString(
                mapper.readValue(fixture("fixtures/nimTake.json"), NimTakeMessage::class.java)
            )

            assertThat(
                mapper.writeValueAsString(nimTakeMessage),
                equalTo(expected)
            )
        }

        it("on deserialization") {
            val expected = mapper.readValue(
                fixture("fixtures/nimTake.json"),
                NimTakeMessage::class.java
            )

            assertThat(
                nimTakeMessage,
                equalTo(expected))
        }
    }
})