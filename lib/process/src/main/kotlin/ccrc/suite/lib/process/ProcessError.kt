package ccrc.suite.lib.process

typealias Timeout = ProcessError.ProcessException.TimeoutError
typealias TerminationError = ProcessError.ProcessException.TerminationError
sealed class ProcessError(open val message: Any?) {
    sealed class ProcessException(
        override val message: Any?,
        open val e: Throwable
    ) : ProcessError(message) {
        data class TimeoutError(
            override val message: Any?,
            override val e: Throwable
        ) : ProcessException(message, e)

        data class TerminationError(
            override val message: Any?,
            override val e: Throwable
        ) : ProcessException(message, e)


    }


}