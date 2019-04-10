package ccrc.suite.commons

typealias BadYaml = Error.BadYamlError
typealias ApiFailure = Error.ApiFailureError
typealias BadInitError = Error.BadInitialization
typealias Unknown = Error.UnknownError
typealias EmptyOption = Error.EmptyOptionError
typealias BadChar = Error.SequenceError.InvalidCharError
sealed class Error {
    abstract val message: Any?
    data class BadYamlError(override val message: Any?) : Error()
    data class ApiFailureError(override val message: Any?) : Error()
    data class UnknownError(override val message: Any? = "Unknown error occured.") : Error()
    data class BadInitialization(override val message: Any?) : Error()
    data class EmptyOptionError(override val message: Any?) : Error()
    sealed class SequenceError: Error(){
        data class InvalidCharError(override val message: Any?) : SequenceError()
    }
}