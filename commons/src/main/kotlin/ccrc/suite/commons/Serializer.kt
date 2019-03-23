package ccrc.suite.commons


import arrow.core.Try
import ccrc.suite.commons.logger.Logger
import com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT
import com.fasterxml.jackson.databind.DeserializationFeature.READ_ENUMS_USING_TO_STRING
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import java.io.File

/**
 * Handles Serialization to an from Json and Yaml.
 */
object Serializer : Logger {
    val jsonMapper: ObjectMapper
    val jsonwriter: ObjectWriter

    init {
        trace { "Initialing Serializer" }
        jsonMapper = jacksonObjectMapper()
        jsonwriter = jsonMapper.writer()
        jsonMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        jsonMapper.configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        configureJsonMapper()
    }

    inline fun <reified T> mapJson(json: File): T {
        return mapJson(json, T::class.java)
    }

    fun <T> mapJson(json: File, clazz: Class<T>): T {
        trace { "Mapping json [$json] to a [$clazz] object" }
        return jsonMapper.readValue(json, clazz).also {
            trace { "Mapped object is [$it]" }
        }
    }

    inline fun <reified T> mapJson(json: String): T {
        return mapJson(json, T::class.java).also {
            trace { "Mapped object is [$it]" }
        }
    }

    inline fun <reified T> parseable(json: String): Try<T> {
        return parseable(json, T::class.java)
    }

    fun <T> parseable(json: String,
                      clazz: Class<T>): Try<T> {
        return Try { mapJson(json, clazz) }
    }

    @Throws(Throwable::class)
    fun <T> mapJson(json: String, clazz: Class<T>): T {
        trace { "Mapping json [$json] to [$clazz] object" }
        return jsonMapper.readValue(json, clazz).also {
            trace { "Mapped object is [$it]" }
        }
    }

    fun <T> writeJson(obj: T): String {
        trace { "Writing [$obj] to json object" }
        return jsonMapper.writeValueAsString(obj)
            .also { trace { "Serialized object to [$it]" } }
    }

    private fun configureJsonMapper() {
        with(jsonMapper) {
            configure(READ_ENUMS_USING_TO_STRING, true)
        }
    }
}