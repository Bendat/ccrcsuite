package ccrc.suite.commons.logger

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.commons.text.WordUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.currentThread
import kotlin.reflect.KClass

inline fun <reified T> klog() = KLog(T::class)

open class KLog(val caller: KClass<*>) : ccrc.suite.commons.logger.Logger {
    override val klog get() = LoggerFactory.getLogger(caller.java)

    constructor(caller: Any) : this(caller::class)
}

class SpekLog(val method: String): KLog(SpekLog::class)

typealias LogLevel = (String) -> Unit

interface Logger {
    @get:JsonIgnore
    val klog: Logger
        get() = LoggerFactory.getLogger(this::class.java)

    /*
    * Retrieves a stacktrace for this log, with a range
    * of potentially useful calls.
    */
    private val stackTrace
        get() = currentThread().stackTrace.mapIndexed { level, it ->
            "\t\t[$level][$it]\n"
        }.filterIndexed { level, _ ->
            (level in 3..9)
        }.joinToString(separator = "")

    fun info(op: Any?) = write(op, true) { klog.info(it) }
    fun trace(op: Any?) = write(op, true) { klog.trace(it) }
    fun debug(op: Any?) = write(op, true) { klog.debug(it) }
    fun error(op: Any?) = write(op, true) { klog.error(it) }
    fun warn(op: Any?) = write(op, true) { klog.warn(it) }

    fun info(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.info(it) }

    fun trace(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.trace(it) }

    fun debug(format: Boolean = true, op: () -> Any) =
        write(op(), format) { klog.debug(it) }

    fun error(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.error(it) }

    fun warn(format: Boolean = true, op: () -> Any?) =
        write(op(), format) { klog.warn(it) }

    private inline fun write(
        msg: Any?,
        format: Boolean = true,
        logtype: LogLevel
    ) {

        val sb = if (format) WordUtils.wrap(
            msg.toString(), 180, "\n\t",
            false, " "
        )//.substring(0, min(3000, msg.toString().length))
        else msg

        logtype(sb.toString())
    }
}