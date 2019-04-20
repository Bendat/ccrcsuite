package ccrc.suite.commons

import java.io.File

typealias BadYaml = Error.BadYamlError
typealias ApiFailure = Error.ApiFailureError
typealias BadInitError = Error.BadInitialization
typealias Unknown = Error.UnknownError
typealias EmptyOption = Error.EmptyOptionError
typealias BadChar = Error.SequenceError.ParseError.InvalidCharError
typealias NoStartingHeader = Error.SequenceError.IOError.NoStartingDescriptionError
typealias SeqReadError = Error.SequenceError.IOError.FileReadError
typealias EmptyFile = Error.SequenceError.IOError.EmptyFileError
typealias SizeMismatch = Error.SequenceError.ParseError.SizeMismatchError

sealed class Error {
    abstract val message: Any?

    data class BadYamlError(override val message: Any?) : Error()
    data class ApiFailureError(override val message: Any?) : Error()
    data class UnknownError(override val message: Any? = "Unknown error occurred.") : Error()
    data class BadInitialization(override val message: Any?) : Error()
    data class EmptyOptionError(override val message: Any?) : Error()
    sealed class SequenceError : Error() {

        sealed class ParseError : SequenceError() {
            data class InvalidCharError(
                override val message: Any?,
                val sequence: String?,
                val badChars: Set<Char>
            ) : ParseError()

            data class SizeMismatchError(
                override val message: Any?,
                val descriptionCount: Int,
                val bodyCount: Int
            ) : ParseError()
        }

        sealed class IOError : SequenceError() {
            data class FileReadError(
                val file: File,
                override val message: Any?
            ) : IOError()

            data class NoStartingDescriptionError(override val message: Any?) : IOError()
            data class EmptyFileError(override val message: Any?) : IOError()
        }

    }

    data class AccumulatedError<TError : Error>(
        override val message: Any?,
        val errors: MutableList<Error> = mutableListOf()
    ) : Error()
}