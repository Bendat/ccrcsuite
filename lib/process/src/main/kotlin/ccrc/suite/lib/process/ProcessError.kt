package ccrc.suite.lib.process

import arrow.core.None

typealias Timeout = ProcessError.ProcessExceptionError.TimeoutError
typealias TerminationError = ProcessError.ProcessExceptionError.TerminationError

sealed class ProcessError {
    abstract val message: Any?

    sealed class ProcessExceptionError : ProcessError() {
        abstract val e: Throwable

        data class TimeoutError(
            override val message: Any?,
            override val e: Throwable
        ) : ProcessExceptionError()

        data class TerminationError(
            override val message: Any?,
            override val e: Throwable
        ) : ProcessExceptionError()
    }

    data class NoProcessError(
        val missingProcess: String,
        override val message: Any? = "The required process was $None"
    ) : ProcessError()
}